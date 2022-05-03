package org.lennardjones.investmanager.configuration;

import org.lennardjones.investmanager.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 *
 * @since 1.2
 * @author lennardjones
 */
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            var optionalAccount = userRepository.findByUsername(username);

            if (optionalAccount.isPresent())
                return optionalAccount.get();

            throw new UsernameNotFoundException("User '" + username + "' not found");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeRequests()
                    .antMatchers("/account", "/product").authenticated()
                    .antMatchers("/product/calculate", "/product/edit").authenticated()
                    .antMatchers("/purchase/delete/*", "/purchase/edit/*",
                            "/purchase/save/*", "/purchase/add").authenticated()
                    .antMatchers("/sale/delete/*", "/sale/edit/*",
                            "/sale/save/*", "/sale/add").authenticated()
                    .antMatchers("/settings", "/settings/editUsername",
                            "/settings/saveNewUsername", "/settings/editPassword",
                            "/settings/saveNewPassword", "/settings/deleteAccount*").authenticated()
                    .antMatchers("/**").permitAll()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/account")
                .and()
                .logout()
                    .logoutSuccessUrl("/login")
                .and()
                .build();
    }
}
