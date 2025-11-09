package com.mikuexpress.mikuexpress.service;

import java.util.List;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikuexpress.mikuexpress.dto.CreateUserAdminDTO;
import com.mikuexpress.mikuexpress.dto.UpdateUserAdminDTO;
import com.mikuexpress.mikuexpress.entity.UserAdmin;
import com.mikuexpress.mikuexpress.enums.Role;
import com.mikuexpress.mikuexpress.mappers.UserAdminMapper;
import com.mikuexpress.mikuexpress.repository.UserAdminRepository;
import com.mikuexpress.mikuexpress.repository.UserGenericRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAdminServcice {

	private final UserAdminRepository userAdminRepository;
    private final UserGenericRepository userGenericRepository; 
    private final PasswordEncoder passwordEncoder;
    private final UserAdminMapper userAdminMapper;
	
	/**
	 * Retorna todos os administradores cadastrados no sistema.
	 * 
	 * @return lista de administradores
	 */
	public List<UserAdmin> getAllAdmins() {
		return userAdminRepository.findAll();
	}
	
	/**
	 * Cria um novo usuário administrador no sistema.
	 * 
	 * Este método:
	 * 1. Valida se o email já não está cadastrado
	 * 2. Converte o DTO para entidade UserAdmin
	 * 3. Define a role como ADMIN
	 * 4. Criptografa a senha usando BCrypt
	 * 5. Salva o usuário no banco de dados
	 * 
	 * @param dto DTO contendo os dados do administrador (nome, email, senha, telefone, CNPJ, nome da organização)
	 * @return UserAdmin criado com os dados persistidos
	 * @throws RuntimeException se o email já estiver cadastrado
	 */
    @Transactional
	public UserAdmin createUserAdmin(CreateUserAdminDTO dto) {
		
    	if(userGenericRepository.existsByEmail(dto.email())) {
    		throw new RuntimeException("E-mail já cadastrado.");
    	}
    	
    	UserAdmin newUser = userAdminMapper.toEntity(dto);
		newUser.setRole(Role.ADMIN);
		newUser.setPassword(passwordEncoder.encode(dto.password()));
		
		return userAdminRepository.save(newUser);
	}
	
	/**
	 * Atualiza os dados de um usuário administrador existente.
	 * 
	 * Este método realiza atualização parcial (PATCH), atualizando apenas os campos que:
	 * - Estão presentes no DTO
	 * - São diferentes dos valores atuais
	 * - Não são nulos ou vazios (para Strings)
	 * 
	 * Campos que podem ser atualizados: nome, telefone, nome da organização
	 * 
	 * @param dto DTO contendo os campos a serem atualizados (todos opcionais)
	 * @param userId ID do usuário administrador a ser atualizado
	 * @return UserAdmin atualizado com os novos dados
	 * @throws RuntimeException se o usuário não for encontrado
	 */
    @Transactional
	public UserAdmin uptadeUserAdmin(UpdateUserAdminDTO dto, long userId) {
    	UserAdmin uptadeUser =  userAdminRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
		
		if(!Objects.equals(uptadeUser.getName(), dto.name()) 
				&& dto.name() != null
				&& !dto.name().trim().isEmpty()) {
			uptadeUser.setName(dto.name());
		}
		
		if(!Objects.equals(uptadeUser.getPhoneNumber(), dto.phoneNumber()) 
				&& dto.phoneNumber() != null
				&& !dto.phoneNumber().trim().isEmpty()) {
			uptadeUser.setPhoneNumber(dto.phoneNumber());
		}
		
		if(!Objects.equals(uptadeUser.getOrganizationName(), dto.organizationName()) 
				&& dto.organizationName() != null
				&& !dto.organizationName().trim().isEmpty()) {
			uptadeUser.setOrganizationName(dto.organizationName());
		}
		
		return userAdminRepository.save(uptadeUser);
	}
	
	/**
	 * Remove um usuário administrador do sistema permanentemente.
	 * 
	 * A operação é irreversível e remove o usuário do banco de dados.
	 * 
	 * @param userId ID do usuário administrador a ser excluído
	 * @throws RuntimeException se o usuário não for encontrado
	 */
    @Transactional
	public void deleteUserAdmin(Long userId) {
    	if (!userAdminRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado.");
        }
    	userAdminRepository.deleteById(userId);
    	
	}
	
	/**
	 * Busca os dados completos de um usuário administrador pelo ID.
	 * 
	 * Retorna todos os dados do administrador incluindo campos específicos como CNPJ e nome da organização.
	 * 
	 * @param id ID do usuário administrador
	 * @return UserAdmin com todos os dados do administrador
	 * @throws RuntimeException se o usuário não for encontrado
	 */
	public UserAdmin getAccountDetails(Long id) {
		return userAdminRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
	}



}
