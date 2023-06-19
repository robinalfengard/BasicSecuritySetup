package com.example.BasicSecuritySetup.config;


import com.example.BasicSecuritySetup.user.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


// Hold all app config such as beans etc
// This annotation will make spring pick it up as an config file
@Configuration
@RequiredArgsConstructor
public class AppConfig {


  private final UserRepository userRepository;

  @Bean
    public UserDetailsService userDetailsService(){
      return username -> userRepository.findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
  }

  // responsible to fetch the user details and decode password
  @Bean
    public AuthenticationProvider authenticationProvider(){
      DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
      // describe where the userdetails should be fetched from
      authenticationProvider.setUserDetailsService(userDetailsService());
      // using the created method to decrypt password
      authenticationProvider.setPasswordEncoder(passwordEncoder());
      return authenticationProvider;
  }


  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
      return config.getAuthenticationManager();
  }


  // Method to decrypt password
  @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }


}
