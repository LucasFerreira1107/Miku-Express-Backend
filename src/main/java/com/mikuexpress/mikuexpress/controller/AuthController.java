package com.mikuexpress.mikuexpress.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mikuexpress.mikuexpress.dto.LoginRequestDTO;
import com.mikuexpress.mikuexpress.dto.LoginResponseDTO;
import com.mikuexpress.mikuexpress.entity.UserGeneric;
import com.mikuexpress.mikuexpress.security.JwtTokenProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * Realiza a autenticação de um usuário no sistema.
	 * 
	 * Este endpoint:
	 * 1. Valida as credenciais (email e senha) do usuário
	 * 2. Autentica o usuário através do AuthenticationManager
	 * 3. Gera um token JWT com as informações do usuário
	 * 4. Retorna o token junto com dados básicos do usuário
	 * 
	 * O token retornado deve ser usado no header Authorization: Bearer {token}
	 * para acessar endpoints protegidos.
	 * 
	 * @param loginRequest DTO contendo email e senha do usuário
	 * @return ResponseEntity com status 200 (OK) e LoginResponseDTO contendo token JWT e dados do usuário, 
	 *         401 (UNAUTHORIZED) se as credenciais forem inválidas,
	 *         500 (INTERNAL_SERVER_ERROR) em caso de erro interno
	 */
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
		try {
			log.info("Tentativa de login para o email: {}", loginRequest.email());
			
			// Autenticar o usuário
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					loginRequest.email(),
					loginRequest.password()
				)
			);

			// Obter o usuário autenticado
			UserGeneric user = (UserGeneric) authentication.getPrincipal();
			
			// Gerar token JWT
			String token = jwtTokenProvider.generateToken(authentication);
			
			log.info("Login realizado com sucesso para o usuário: {}", user.getEmail());
			
			// Retornar resposta com token
			LoginResponseDTO response = LoginResponseDTO.of(
				token,
				user.getEmail(),
				user.getName(),
				user.getRole(),
				user.getId()
			);
			
			return ResponseEntity.ok(response);
			
		} catch (BadCredentialsException e) {
			log.warn("Credenciais inválidas para o email: {}", loginRequest.email());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (Exception e) {
			log.error("Erro ao realizar login para o email: {}", loginRequest.email(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
