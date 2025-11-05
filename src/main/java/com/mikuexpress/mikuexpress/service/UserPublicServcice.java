package com.mikuexpress.mikuexpress.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikuexpress.mikuexpress.dto.CreateUserPublicDTO;
import com.mikuexpress.mikuexpress.dto.UpdateUserPublicDTO;
import com.mikuexpress.mikuexpress.entity.UserPublic;
import com.mikuexpress.mikuexpress.enums.Role;
import com.mikuexpress.mikuexpress.mappers.UserPublicMapper;
import com.mikuexpress.mikuexpress.repository.UserGenericRepository;
import com.mikuexpress.mikuexpress.repository.UserPublicRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPublicServcice {

	private final UserPublicRepository userPublicRepository;
    private final UserGenericRepository userGenericRepository; 
    private final PasswordEncoder passwordEncoder;
    private final UserPublicMapper userPublicMapper;
	
	/**
	 * Cria um novo usuário cliente (UserPublic) no sistema.
	 * 
	 * Este método:
	 * 1. Valida se o email já não está cadastrado
	 * 2. Converte o DTO para entidade UserPublic
	 * 3. Define a role como CLIENT
	 * 4. Criptografa a senha usando BCrypt
	 * 5. Salva o usuário no banco de dados
	 * 
	 * @param dto DTO contendo os dados do cliente (nome, email, senha, telefone, CPF, data de nascimento, gênero)
	 * @return UserPublic criado com os dados persistidos
	 * @throws RuntimeException se o email já estiver cadastrado
	 */
    @Transactional
	public UserPublic createUserPublic(CreateUserPublicDTO dto) {
		
    	if(userGenericRepository.existsByEmail(dto.email())) {
    		throw new RuntimeException("E-mail já cadastrado.");
    	}
    	
    	UserPublic newUser = userPublicMapper.toEntity(dto);
		newUser.setRole(Role.CLIENT);
		newUser.setPassword(passwordEncoder.encode(dto.password()));
		
		return userPublicRepository.save(newUser);
	}
	
	/**
	 * Atualiza os dados de um usuário cliente existente.
	 * 
	 * Este método realiza atualização parcial (PATCH), atualizando apenas os campos que:
	 * - Estão presentes no DTO
	 * - São diferentes dos valores atuais
	 * - Não são nulos ou vazios (para Strings)
	 * 
	 * Campos que podem ser atualizados: nome, telefone, data de nascimento, gênero
	 * 
	 * @param dto DTO contendo os campos a serem atualizados (todos opcionais)
	 * @param userId ID do usuário cliente a ser atualizado
	 * @return UserPublic atualizado com os novos dados
	 * @throws RuntimeException se o usuário não for encontrado
	 */
    @Transactional
	public UserPublic uptadeUserPublic(UpdateUserPublicDTO dto, Long userId) {
		UserPublic uptadeUser =  userPublicRepository.findById(userId)
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
		
		if(!Objects.equals(uptadeUser.getDateOfBirth(), dto.dateOfBirth()) 
				&& dto.dateOfBirth() != null) {
			uptadeUser.setDateOfBirth(dto.dateOfBirth());
		}
		
		if(!Objects.equals(uptadeUser.getGender(), dto.gender()) 
				&& dto.gender() != null) {
			uptadeUser.setGender(dto.gender());
		}
		
		
		return userPublicRepository.save(uptadeUser);
	}
	
	/**
	 * Remove um usuário cliente do sistema permanentemente.
	 * 
	 * A operação é irreversível e remove o usuário do banco de dados.
	 * 
	 * @param userId ID do usuário cliente a ser excluído
	 * @throws RuntimeException se o usuário não for encontrado
	 */
    @Transactional
	public void deleteUserPublic(Long userId) {
    	if (!userPublicRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        userPublicRepository.deleteById(userId);
    	
	}
	
	/**
	 * Busca os dados completos de um usuário cliente pelo ID.
	 * 
	 * Retorna todos os dados do cliente incluindo campos específicos como CPF, data de nascimento e gênero.
	 * 
	 * @param id ID do usuário cliente
	 * @return UserPublic com todos os dados do cliente
	 * @throws RuntimeException se o usuário não for encontrado
	 */
	public UserPublic getAccountDetails(Long id) {
		return userPublicRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
	}



}
