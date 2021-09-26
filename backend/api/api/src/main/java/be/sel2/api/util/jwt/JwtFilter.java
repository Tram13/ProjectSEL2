package be.sel2.api.util.jwt;

import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.users.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Fetches UserData from JWT tokens in the Authorization header
 *
 * Is not applied to `/` and `/auth/**`
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Is not applied to `/` and `/auth/**`
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.matches("^/(auth(/[a-zA-Z]*)?)?$");
    }

    /**
     * Fetches UserData from JWT tokens in the Authorization header
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        long id = 0;
        String token = null;
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            token = tokenHeader.substring(7);
            try {
                id = jwtUtil.extractId(token);
            } catch (ExpiredJwtException ex) {
                throw new ForbiddenException("Token has expired");
            } catch (SignatureException ex) {
                throw new ForbiddenException("Token has an invalid signature");
            } catch (MalformedJwtException ex) {
                throw new ForbiddenException("Token is malformed");
            }
        } else {
            throw new ForbiddenException("Authorization using Bearer token required");
        }
        if (id != 0 && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserById(id);
            if (Boolean.TRUE.equals(jwtUtil.validateToken(token))) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
