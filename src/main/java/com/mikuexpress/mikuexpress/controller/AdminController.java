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

import com.mikuexpress.mikuexpress.dto.CreatePackageDTO;
import com.mikuexpress.mikuexpress.dto.CreateUserAdminDTO;
import com.mikuexpress.mikuexpress.dto.CreatedPackageDTO;
import com.mikuexpress.mikuexpress.dto.StatusUpdateDTO;
import com.mikuexpress.mikuexpress.dto.StatusUpdatedDTO;
import com.mikuexpress.mikuexpress.dto.UpdateUserAdminDTO;
import com.mikuexpress.mikuexpress.entity.Order;
import com.mikuexpress.mikuexpress.entity.UserAdmin;
import com.mikuexpress.mikuexpress.service.OrderService;
import com.mikuexpress.mikuexpress.service.SecurityService;
import com.mikuexpress.mikuexpress.service.UserAdminServcice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

	private final UserAdminServcice userAdminService;
	private final SecurityService securityService;
	private final OrderService orderService;
	
	/**
	 * Registra um novo usuário administrador no sistema.
	 * 
	 * Endpoint público que permite criar uma conta de administrador.
	 * 
	 * @param dto DTO contendo os dados do administrador (nome, email, senha, telefone, CNPJ, nome da organização)
	 * @return ResponseEntity com status 201 (CREATED) e o UserAdmin criado
	 */
	@PostMapping("/register")
	public ResponseEntity<UserAdmin> createUserAdmin(@RequestBody CreateUserAdminDTO dto) {
		log.info("Registrando novo usuario administrativo: {}", dto.email());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(userAdminService.createUserAdmin(dto));
	}
	
	/**
	 * Retorna os dados completos da conta do administrador autenticado.
	 * 
	 * @return ResponseEntity com status 200 (OK) e os dados do UserAdmin autenticado
	 */
	@GetMapping("/account")
	public ResponseEntity<UserAdmin> getAccountDetails() {
		return ResponseEntity.ok(securityService.getCurrentUserAdmin());
	}
	
	/**
	 * Atualiza os dados da conta do administrador autenticado.
	 * 
	 * Realiza atualização parcial (PATCH) dos dados do administrador logado.
	 * Apenas os campos informados no DTO serão atualizados.
	 * 
	 * @param dto DTO contendo os campos a serem atualizados (todos opcionais)
	 * @return ResponseEntity com status 200 (OK) e o UserAdmin atualizado
	 */
	@PatchMapping("/account")
	public ResponseEntity<UserAdmin> updateUserAdmin(@RequestBody UpdateUserAdminDTO dto) {
		Long userId = securityService.getCurrentUserId();
		log.info("Atualizando dados da conta do usuário ID: {}", userId);
		
		UserAdmin updatedUser = userAdminService.uptadeUserAdmin(dto, userId);
		return ResponseEntity.ok(updatedUser);
	}
	
	/**
	 * Remove a conta do administrador autenticado do sistema.
	 * 
	 * A operação é irreversível e remove permanentemente o usuário administrador logado.
	 * 
	 * @return ResponseEntity com status 204 (NO_CONTENT) em caso de sucesso
	 */
	@DeleteMapping("/account")
	public ResponseEntity<?> deleteUserAdmin() {
		Long userId = securityService.getCurrentUserId();
		log.info("Deletando conta do usuário ID: {}", userId);
		
		userAdminService.deleteUserAdmin(userId);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Retorna todos os administradores cadastrados no sistema.
	 * 
	 * @return ResponseEntity com status 200 (OK) e a lista de administradores
	 */
	@GetMapping("/users")
	public ResponseEntity<List<UserAdmin>> getAllAdmins() {
		return ResponseEntity.ok(userAdminService.getAllAdmins());
	}
	
	/**
	 * Atualiza os dados de um administrador específico pelo ID.
	 * 
	 * @param adminId ID do administrador a ser atualizado
	 * @param dto DTO com os dados a serem atualizados
	 * @return ResponseEntity com o administrador atualizado
	 */
	@PatchMapping("/users/{id}")
	public ResponseEntity<UserAdmin> updateAdminById(
			@PathVariable("id") Long adminId,
			@RequestBody UpdateUserAdminDTO dto) {
		return ResponseEntity.ok(userAdminService.uptadeUserAdmin(dto, adminId));
	}
	
	/**
	 * Remove um administrador do sistema pelo ID.
	 * 
	 * @param adminId ID do administrador a ser removido
	 * @return ResponseEntity com status 204 em caso de sucesso
	 */
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteAdminById(@PathVariable("id") Long adminId) {
		userAdminService.deleteUserAdmin(adminId);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Retorna todos os pedidos cadastrados no sistema.
	 * 
	 * Este endpoint permite que administradores visualizem todos os pedidos,
	 * independente do cliente.
	 * 
	 * @return ResponseEntity com status 200 (OK) e lista de todos os pedidos
	 */
	@GetMapping("/orders")
	public ResponseEntity<List<Order>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAllOrders());
	}
	
	/**
	 * Busca um pedido específico pelo ID.
	 * 
	 * Retorna todos os dados do pedido incluindo o histórico completo de atualizações de status.
	 * 
	 * @param orderId ID único do pedido no banco de dados
	 * @return ResponseEntity com status 200 (OK) e os dados do pedido encontrado
	 */
	@GetMapping("/orders/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable("id") Long orderId) {
		return ResponseEntity.ok(orderService.getOrderById(orderId));
	}
	
	/**
	 * Adiciona uma nova atualização de status a um pedido existente.
	 * 
	 * Este endpoint permite que administradores atualizem o status de qualquer pedido,
	 * enviando automaticamente um e-mail de notificação ao cliente.
	 * 
	 * @param dto DTO contendo o status, localização de origem e destino
	 * @param orderId ID do pedido que terá o status atualizado
	 * @return ResponseEntity com status 200 (OK) e os dados da atualização de status criada
	 */
	@PatchMapping("/orders/{id}/status")
	public ResponseEntity<StatusUpdatedDTO> addStatusUpdate(
			@RequestBody StatusUpdateDTO dto,
			@PathVariable("id") Long orderId) {
		StatusUpdatedDTO statusUpdated = orderService.addStatusUpdate(dto, orderId);
		return ResponseEntity.ok(statusUpdated);
	}
	
	/**
	 * Remove um pedido do sistema permanentemente.
	 * 
	 * Este endpoint permite que administradores excluam qualquer pedido.
	 * A operação é irreversível e remove o pedido e todas suas atualizações de status.
	 * 
	 * @param orderId ID do pedido a ser excluído
	 * @return ResponseEntity com status 204 (NO_CONTENT) em caso de sucesso
	 */
	@DeleteMapping("/orders/{id}")
	public ResponseEntity<?> deleteOrderById(@PathVariable("id") Long orderId) {
		orderService.deleteOrder(orderId);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Cria um novo pedido/pacote no sistema.
	 * 
	 * Este endpoint permite que administradores criem pedidos para clientes.
	 * O processo inclui:
	 * - Busca de endereços via ViaCEP
	 * - Cálculo de distância via Google Maps
	 * - Cálculo automático do preço do frete
	 * - Geração de código de rastreio
	 * - Envio de e-mail de notificação ao cliente
	 * 
	 * @param dto DTO contendo os dados do pedido (CEPs, dados do cliente, peso)
	 * @return ResponseEntity com status 201 (CREATED) e os dados do pedido criado incluindo preço e código de rastreio
	 */
	@PostMapping("/orders")
	public ResponseEntity<CreatedPackageDTO> createPackage(@RequestBody CreatePackageDTO dto){
		log.info("Registrando novo pacote");
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(orderService.createPackage(dto));
	}
}
