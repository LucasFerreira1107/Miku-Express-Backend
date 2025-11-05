package com.mikuexpress.mikuexpress.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mikuexpress.mikuexpress.dto.CreateUserPublicDTO;
import com.mikuexpress.mikuexpress.dto.UpdateUserPublicDTO;
import com.mikuexpress.mikuexpress.entity.Order;
import com.mikuexpress.mikuexpress.entity.UserPublic;
import com.mikuexpress.mikuexpress.service.OrderService;
import com.mikuexpress.mikuexpress.service.SecurityService;
import com.mikuexpress.mikuexpress.service.UserPublicServcice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

	private final UserPublicServcice userPublicService;
	private final SecurityService securityService;
	private final OrderService orderService;

	/**
	 * Registra um novo usuário cliente no sistema.
	 * 
	 * Endpoint público que permite criar uma conta de cliente.
	 * 
	 * @param dto DTO contendo os dados do cliente (nome, email, senha, telefone, CPF, data de nascimento, gênero)
	 * @return ResponseEntity com status 201 (CREATED) e o UserPublic criado
	 */
	@PostMapping("/register")
	public ResponseEntity<UserPublic> createUserPublic(@RequestBody CreateUserPublicDTO dto) {
		log.info("Registrando novo cliente: {}", dto.email());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(userPublicService.createUserPublic(dto));
	}

	/**
	 * Retorna os dados completos da conta do cliente autenticado.
	 * 
	 * Retorna todos os dados do cliente incluindo campos específicos como CPF, data de nascimento e gênero.
	 * 
	 * @return ResponseEntity com status 200 (OK) e os dados do UserPublic autenticado
	 */
	@GetMapping("/account")
	public ResponseEntity<UserPublic> getAccountDetails() {
		UserPublic currentUser = securityService.getCurrentUserPublic();
		log.info("Buscando dados da conta do usuário ID: {}", currentUser.getId());
		
		// Retorna o usuário completo já carregado, evitando query duplicada
		return ResponseEntity.ok(currentUser);
	}

	/**
	 * Atualiza os dados da conta do cliente autenticado.
	 * 
	 * Realiza atualização parcial (PATCH) dos dados do cliente logado.
	 * Apenas os campos informados no DTO serão atualizados.
	 * 
	 * @param dto DTO contendo os campos a serem atualizados (todos opcionais)
	 * @return ResponseEntity com status 200 (OK) e o UserPublic atualizado
	 */
	@PatchMapping("/account")
	public ResponseEntity<UserPublic> updateUserPublic(@RequestBody UpdateUserPublicDTO dto) {
		Long userId = securityService.getCurrentUserId();
		log.info("Atualizando dados da conta do usuário ID: {}", userId);
		
		UserPublic updatedUser = userPublicService.uptadeUserPublic(dto, userId);
		return ResponseEntity.ok(updatedUser);
	}

	/**
	 * Remove a conta do cliente autenticado do sistema.
	 * 
	 * A operação é irreversível e remove permanentemente o usuário cliente logado.
	 * 
	 * @return ResponseEntity com status 204 (NO_CONTENT) em caso de sucesso
	 */
	@DeleteMapping("/account")
	public ResponseEntity<?> deleteUserPublic() {
		Long userId = securityService.getCurrentUserId();
		log.info("Deletando conta do usuário ID: {}", userId);
		
		userPublicService.deleteUserPublic(userId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Retorna todos os pedidos do cliente autenticado.
	 * 
	 * Este endpoint filtra automaticamente os pedidos pelo email do cliente logado,
	 * garantindo que o cliente veja apenas seus próprios pedidos.
	 * 
	 * @return ResponseEntity com status 200 (OK) e lista de pedidos do cliente autenticado
	 */
	@GetMapping("/orders")
	public ResponseEntity<List<Order>> getClientOrders() {
		String userEmail = securityService.getCurrentUserEmail();
		log.info("Buscando pedidos do cliente: {}", userEmail);
		
		List<Order> orders = orderService.getOrdersByCustomerEmail(userEmail);
		return ResponseEntity.ok(orders);
	}

	/**
	 * Busca um pedido específico do cliente autenticado pelo ID.
	 * 
	 * Este endpoint inclui validação de segurança para garantir que o cliente
	 * só possa acessar seus próprios pedidos. Se o pedido pertencer a outro cliente,
	 * retorna status 403 (FORBIDDEN).
	 * 
	 * @param orderId ID único do pedido no banco de dados
	 * @return ResponseEntity com status 200 (OK) e os dados do pedido, ou 403 (FORBIDDEN) se o pedido não pertencer ao cliente
	 */
	@GetMapping("/orders/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
		String userEmail = securityService.getCurrentUserEmail();
		log.info("Buscando pedido ID {} do cliente: {}", orderId, userEmail);
		
		Order order = orderService.getOrderById(orderId);
		
		// Verificar se o pedido pertence ao cliente autenticado
		if (!order.getCustomerEmail().equals(userEmail)) {
			log.warn("Cliente {} tentou acessar pedido de outro cliente", userEmail);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		return ResponseEntity.ok(order);
	}
}
