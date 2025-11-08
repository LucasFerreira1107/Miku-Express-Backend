package com.mikuexpress.mikuexpress.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mikuexpress.mikuexpress.entity.UserGeneric;
import com.mikuexpress.mikuexpress.repository.UserGenericRepository;
import com.mikuexpress.mikuexpress.security.JwtCustomAuthenticationFilter;
import com.mikuexpress.mikuexpress.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@Slf4j
public class SecurityConfiguration {
	
	@Bean
	public JwtCustomAuthenticationFilter jwtCustomAuthenticationFilter(
		JwtTokenProvider jwtTokenProvider,
		UserDetailsService userDetailsService
	) {
		return new JwtCustomAuthenticationFilter(jwtTokenProvider, userDetailsService);
	}

	@Bean
	public SecurityFilterChain sourceSecurityFilterChain(
		HttpSecurity http,
		JwtCustomAuthenticationFilter jwtTokenFilter
	) throws Exception {

		return http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authorize -> {
				authorize.requestMatchers(
					"/error",
					"/api/auth/**",
					"/api/publics/**",
					"/api/admins/register",
					"/api/clients/register"
				).permitAll();
				authorize.requestMatchers("/api/admins/**").hasRole("ADMIN");
				authorize.requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "CLIENT");
				authorize.anyRequest().authenticated();
			})
			.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	// Configura o prefixo role
	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	public UserDetailsService userDetailsService(UserGenericRepository repository) {
		return username -> {
			log.debug("UserDetailsService: Buscando usuário com email: {}", username);
	        
			UserGeneric userEntity = repository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + username));
			
			log.debug("Usuário encontrado: {} (ID: {})", userEntity.getName(), userEntity.getId());
	        
	        if (userEntity.getRole() == null) {
	            log.error("AVISO: Usuário '{}' não possui role definida", username);
	            throw new IllegalStateException("Usuário sem role definida: " + username);
	        }
			
			return userEntity;
		};
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
