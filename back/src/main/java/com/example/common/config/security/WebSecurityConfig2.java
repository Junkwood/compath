package com.example.common.config.security;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

//@Configuration
//@EnableWebSecurity
class WebSecurityConfig2 {

//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		// @formatter:off
//		http
//			.authorizeHttpRequests((requests) -> requests
//				.requestMatchers("/", "/home","/login","/**").permitAll()
//				.requestMatchers("/admin").hasRole("ADMIN")
//				.anyRequest().authenticated()
//			)
//			.formLogin((form) -> form
//				.loginPage("/login")
//				//.loginProcessingUrl(null)
//				.successHandler(authenticationSuccessHandler())
//				.permitAll()
//			)
//			.logout(LogoutConfigurer::permitAll)
//			.csrf(c->c.disable());
//		// @formatter:on
//
//		return http.build();
//	}
//
//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//	@Bean
//	public AuthenticationSuccessHandler authenticationSuccessHandler() {
//		return new CustomLoginSuccessHandler();
//	}

//	@Bean
//    UserDetailsService userDetailsService(PasswordEncoder encoder) {
//		String password = encoder.encode("1234");
//		UserDetails user = User.withUsername("user").password(password).roles("USER").build();
//		UserDetails admin = User.withUsername("admin").password(password).roles("ADMIN").build();
//		return new InMemoryUserDetailsManager(user,admin);
//	}

}