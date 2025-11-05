package com.mikuexpress.mikuexpress.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mikuexpress.mikuexpress.entity.UserGeneric;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtCustomAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, java.io.IOException {

		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt)) {
				try {
					String username = jwtTokenProvider.getUsernameFromToken(jwt);
					log.debug("Token JWT encontrado para usuário: {}", username);
					
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					
					if (jwtTokenProvider.validateToken(jwt, userDetails)) {
						if (userDetails instanceof UserGeneric user) {
							CustomAuthentication authentication = new CustomAuthentication(user);
							SecurityContextHolder.getContext().setAuthentication(authentication);
							log.debug("Autenticação JWT configurada para usuário: {} (ID: {})", user.getEmail(), user.getId());
						} else {
							log.warn("UserDetails retornado não é uma instância de UserGeneric: {}", 
									userDetails != null ? userDetails.getClass().getName() : "null");
							// Limpar contexto se o tipo não for correto
							SecurityContextHolder.clearContext();
						}
					} else {
						log.warn("Token JWT inválido para usuário: {}", username);
						SecurityContextHolder.clearContext();
					}
				} catch (Exception ex) {
					log.error("Erro ao processar token JWT", ex);
					// Limpar o contexto em caso de erro
					SecurityContextHolder.clearContext();
				}
			} else {
				// Se não há token JWT e há uma autenticação anterior no contexto, verificar se é válida
				var existingAuth = SecurityContextHolder.getContext().getAuthentication();
				if (existingAuth != null && !(existingAuth instanceof CustomAuthentication)) {
					// Limpar autenticações inválidas (que não sejam CustomAuthentication)
					log.debug("Limpando autenticação inválida do tipo: {}", existingAuth.getClass().getName());
					SecurityContextHolder.clearContext();
				}
			}
		} catch (Exception ex) {
			log.error("Erro crítico no filtro JWT", ex);
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
