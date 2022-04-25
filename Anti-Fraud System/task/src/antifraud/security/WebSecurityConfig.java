package antifraud.security;

import antifraud.entity.UserRole;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/actuator/shutdown").permitAll() // needs to run test
                .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole(UserRole.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(UserRole.ADMINISTRATOR.name(), UserRole.SUPPORT.name())
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole(UserRole.MERCHANT.name())
                .mvcMatchers(HttpMethod.PUT, "/api/auth/access").hasRole(UserRole.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.PUT, "/api/auth/role").hasRole(UserRole.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole(UserRole.MERCHANT.name())
                .mvcMatchers(HttpMethod.DELETE,"/api/antifraud/suspicious-ip/*").hasRole(UserRole.SUPPORT.name())
                .mvcMatchers(HttpMethod.GET,"/api/antifraud/suspicious-ip").hasRole(UserRole.SUPPORT.name())
                .mvcMatchers(HttpMethod.POST,"/api/antifraud/suspicious-ip").hasRole(UserRole.SUPPORT.name())
                .mvcMatchers("/api/antifraud/stolencard").hasRole(UserRole.SUPPORT.name())
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Autowired
    void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
        auth.inMemoryAuthentication().passwordEncoder(getEncoder());
    }

    @Bean
    public BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
