package com.mikuexpress.mikuexpress.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mikuexpress.mikuexpress.entity.UserAdmin;
import com.mikuexpress.mikuexpress.entity.UserGeneric;
import com.mikuexpress.mikuexpress.entity.UserPublic;
import com.mikuexpress.mikuexpress.repository.UserPublicRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {
	private final UserPublicRepository userPublicRepository;
	private final UserAdminServcice userAdminService;

	/**
	 * Obtém o usuário genérico atualmente autenticado do SecurityContext.
	 * 
	 * Este método extrai o usuário autenticado do contexto de segurança do Spring.
	 * Não realiza consulta ao banco de dados, utilizando o objeto já carregado em memória.
	 * Use este método quando precisar de campos comuns (id, email, name, phoneNumber, role).
	 * 
	 * @return UserGeneric do usuário autenticado
	 * @throws UsernameNotFoundException se nenhum usuário estiver autenticado ou se o principal não for UserGeneric
	 */
	public UserGeneric getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null) {
			log.error("Nenhuma autenticação encontrada no SecurityContext");
			throw new UsernameNotFoundException("Usuário não autenticado - nenhuma autenticação encontrada");
		}

		if (!authentication.isAuthenticated()) {
			log.error("Autenticação encontrada mas não está autenticada");
			throw new UsernameNotFoundException("Usuário não autenticado - autenticação inválida");
		}

		Object principal = authentication.getPrincipal();
		
		if (principal instanceof UserGeneric user) {
			log.debug("Usuário autenticado encontrado: {} (ID: {})", user.getEmail(), user.getId());
			return user;
		}

		log.error("Principal não é uma instância de UserGeneric. Tipo encontrado: {}, Valor: {}", 
				principal != null ? principal.getClass().getName() : "null",
				principal != null ? principal.toString() : "null");
		
		// Tentar obter o username se for uma String e buscar no banco
		if (principal instanceof String username) {
			log.warn("Principal é uma String (username). Tentando buscar UserGeneric no banco...");
			throw new UsernameNotFoundException(
					"Autenticação inválida - principal é String ao invés de UserGeneric. " +
					"Isso geralmente acontece quando o JWT filter não foi executado corretamente. " +
					"Certifique-se de que o token JWT está sendo enviado no header Authorization: Bearer {token}");
		}

		throw new UsernameNotFoundException(
				"Principal não é uma instância de UserGeneric: " + 
				(principal != null ? principal.getClass().getName() : "null"));
	}

	/**
	 * Obtém o ID do usuário atualmente autenticado.
	 * 
	 * Método de conveniência que retorna apenas o ID do usuário logado.
	 * Não realiza consulta ao banco de dados.
	 * 
	 * @return Long ID do usuário autenticado
	 * @throws UsernameNotFoundException se nenhum usuário estiver autenticado
	 */
	public Long getCurrentUserId() {
		return getCurrentUser().getId();
	}

	/**
	 * Obtém o email do usuário atualmente autenticado.
	 * 
	 * Método de conveniência que retorna apenas o email do usuário logado.
	 * Não realiza consulta ao banco de dados.
	 * 
	 * @return String email do usuário autenticado
	 * @throws UsernameNotFoundException se nenhum usuário estiver autenticado
	 */
	public String getCurrentUserEmail() {
		return getCurrentUser().getEmail();
	}

	/**
	 * Obtém o UserPublic atualmente autenticado.
	 * 
	 * Este método busca no banco de dados para garantir que o usuário autenticado
	 * é realmente do tipo UserPublic e retorna os campos específicos (cpf, dateOfBirth, gender).
	 * Use apenas quando precisar de campos específicos de cliente, pois realiza uma consulta ao banco.
	 * 
	 * @return UserPublic do usuário autenticado com todos os campos específicos
	 * @throws RuntimeException se o usuário não for do tipo UserPublic
	 */
	public UserPublic getCurrentUserPublic() {
		UserGeneric userGeneric = getCurrentUser();
		
		return userPublicRepository.findById(userGeneric.getId())
			.orElseThrow(() -> {
				log.error("Usuário autenticado {} não é um UserPublic", userGeneric.getId());
				return new RuntimeException("Usuário não é do tipo Cliente");
			});
	}
	
	/**
	 * Obtém o UserAdmin atualmente autenticado.
	 * 
	 * Este método busca no banco de dados para garantir que o usuário autenticado
	 * é realmente do tipo UserAdmin e retorna os campos específicos (cnpj, organizationName).
	 * Use apenas quando precisar de campos específicos de administrador, pois realiza uma consulta ao banco.
	 * 
	 * @return UserAdmin do usuário autenticado com todos os campos específicos
	 * @throws RuntimeException se o usuário não for do tipo UserAdmin
	 */
	public UserAdmin getCurrentUserAdmin() {
		UserGeneric userGeneric = getCurrentUser();
		
		return userAdminService.getAccountDetails(userGeneric.getId());
	}

	/**
	 * Verifica se o usuário atual é um cliente (CLIENT).
	 * 
	 * Este método verifica a role do usuário autenticado sem lançar exceção
	 * caso não haja usuário autenticado.
	 * 
	 * @return true se o usuário for do tipo CLIENT, false caso contrário ou se não houver usuário autenticado
	 */
	public boolean isCurrentUserClient() {
		try {
			UserGeneric user = getCurrentUser();
			return user.getRole().name().equals("CLIENT");
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Verifica se o usuário atual é um administrador (ADMIN).
	 * 
	 * Este método verifica a role do usuário autenticado sem lançar exceção
	 * caso não haja usuário autenticado.
	 * 
	 * @return true se o usuário for do tipo ADMIN, false caso contrário ou se não houver usuário autenticado
	 */
	public boolean isCurrentUserAdmin() {
		try {
			UserGeneric user = getCurrentUser();
			return user.getRole().name().equals("ADMIN");
		} catch (Exception e) {
			return false;
		}
	}
}
