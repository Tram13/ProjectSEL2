package be.sel2.api;

import be.sel2.api.authentication.JwtAuthenticationEntryPoint;
import be.sel2.api.users.UserService;
import be.sel2.api.util.filter_chain.FilterChainExceptionHandler;
import be.sel2.api.util.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final JwtFilter jwtFilter;
    private final FilterChainExceptionHandler filterChainExceptionHandler;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public WebSecurityConfig(UserService userService, JwtFilter jwtFilter,
                             FilterChainExceptionHandler filterChainExceptionHandler, JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.userService = userService;
        this.jwtFilter = jwtFilter;
        this.filterChainExceptionHandler = filterChainExceptionHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //.requiresChannel()
        //.anyRequest()
        //.requiresSecure()
        //.and()
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/", "/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .cors()
                .and()
                .csrf().disable(); // NOSONAR

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterChainExceptionHandler, JwtFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}


