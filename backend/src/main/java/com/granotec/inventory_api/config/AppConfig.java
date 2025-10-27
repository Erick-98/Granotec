package com.granotec.inventory_api.config;

import com.granotec.inventory_api.permission.Permission;
import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService(){
          return username -> repository.findByEmail(username)
                  .map(user -> {
                      List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                      Role role = user.getRole();

                      if(role != null){
                          authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                          if(role.getPermissions() != null){
                              for(Permission permission : role.getPermissions()){
                                  authorities.add(new SimpleGrantedAuthority(permission.getName()));
                              }
                          }
                      }

                      return org.springframework.security.core.userdetails.User
                              .builder()
                              .username(user.getEmail())
                              .password(user.getPassword())
                              .authorities(authorities)
                              .build();
                  })
                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
