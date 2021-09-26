package be.sel2.api.controllers;

import be.sel2.api.authentication.JwtRefreshRequest;
import be.sel2.api.authentication.JwtRefreshResponse;
import be.sel2.api.authentication.JwtRequest;
import be.sel2.api.authentication.JwtResponse;
import be.sel2.api.dtos.UserInfoDTO;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.repositories.UserRepository;
import be.sel2.api.util.jwt.JwtUtil;
import be.sel2.api.util.specifications.DefaultSpecification;
import be.sel2.api.util.specifications.SearchCriteria;
import io.jsonwebtoken.ExpiredJwtException;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserController userController;
    private final UserRepository userRepository;
    private static final String TOKEN_EXPIRED_MESSAGE = "Refresh token is expired";

    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                                    UserController userController, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userController = userController;
        this.userRepository = userRepository;
    }

    //AUTH: ALLOW ALL
    @GetMapping
    public EntityModel<Object> getHrefs() {
        return EntityModel.of(Map.of(),
                linkTo(AuthenticationController.class).withSelfRel(),
                linkTo(methodOn(AuthenticationController.class).register(new UserInfoDTO())).withRel("register"),
                linkTo(methodOn(AuthenticationController.class).authenticate(new JwtRequest())).withRel("login"),
                linkTo(methodOn(AuthenticationController.class).refreshSession(new JwtRefreshRequest())).withRel("refreshSessionToken"),
                linkTo(methodOn(AuthenticationController.class).refreshToken(new JwtRefreshRequest())).withRel("refreshToken"),
                linkTo(methodOn(AuthenticationController.class).passwordReset()).withRel("passwordReset"),
                linkTo(methodOn(AuthenticationController.class).passwordResetForm()).withRel("passwordResetForm"),
                linkTo(methodOn(AuthenticationController.class).emailConfirmation()).withRel("emailConfirmation"),
                linkTo(methodOn(AuthenticationController.class).resendEmailConfirmation()).withRel("resendEmailConfirmation")
        );
    }

    //AUTH: ALLOW ALL
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody @Valid JwtRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),
                            request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw ex;
        } catch (AuthenticationException ex) {
            throw new ForbiddenException(ex.getMessage());
        }
        DefaultSpecification<UserInfo> specification = new DefaultSpecification<>();
        specification.add(new SearchCriteria("email", request.getEmail()));
        UserInfo user = userRepository.findOne(specification).orElseThrow(RuntimeException::new);
        final String sessionToken = jwtUtil.generateToken(user.getId());
        final String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        return ResponseEntity.ok(new JwtResponse(sessionToken, refreshToken));
    }

    //AUTH: ALLOW ALL
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<?> register(@RequestBody UserInfoDTO user) {
        user.setRole(UserInfo.Userrole.CUSTOMER.toString());
        userController.registerUser(user);
        return HttpEntity.EMPTY;
    }

    //AUTH: ALLOW ALL
    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody @Valid JwtRefreshRequest request) {
        try {
            Long id = jwtUtil.extractId(request.getRefreshToken());
            if (Boolean.TRUE.equals(jwtUtil.validateRefreshToken(request.getRefreshToken()))) {
                final String sessionToken = jwtUtil.generateToken(id);
                final String refreshToken = jwtUtil.generateRefreshToken(id);
                return ResponseEntity.ok(new JwtResponse(sessionToken, refreshToken));
            }
            throw new ForbiddenException(TOKEN_EXPIRED_MESSAGE);
        } catch (ExpiredJwtException ex) {
            throw new ForbiddenException(TOKEN_EXPIRED_MESSAGE);
        }
    }

    //AUTH: ALLOW ALL
    @PostMapping("/refreshSessionToken")
    public ResponseEntity<JwtRefreshResponse> refreshSession(@RequestBody @Valid JwtRefreshRequest request) {
        try {
            Long id = jwtUtil.extractId(request.getRefreshToken());
            if (Boolean.TRUE.equals(jwtUtil.validateRefreshToken(request.getRefreshToken()))) {
                final String sessionToken = jwtUtil.generateToken(id);
                return ResponseEntity.ok(new JwtRefreshResponse(sessionToken));
            }
            throw new ForbiddenException(TOKEN_EXPIRED_MESSAGE);
        } catch (ExpiredJwtException ex) {
            throw new ForbiddenException(TOKEN_EXPIRED_MESSAGE);
        }

    }

    //AUTH: ALLOW ALL
    @PostMapping("/passwordReset")
    public HttpEntity<Object> passwordReset() {
        throw new NotYetImplementedException();
    }

    //AUTH: ALLOW ALL
    @PostMapping("/passwordReset/form")
    public HttpEntity<Object> passwordResetForm() {
        throw new NotYetImplementedException();
    }

    //AUTH: ALLOW ALL
    @PostMapping("/emailConfirmation")
    public HttpEntity<Object> emailConfirmation() {
        throw new NotYetImplementedException();
    }

    //AUTH: ALLOW ALL
    @PostMapping("/resendEmailConfirmation")
    public HttpEntity<Object> resendEmailConfirmation() {
        throw new NotYetImplementedException();
    }


}
