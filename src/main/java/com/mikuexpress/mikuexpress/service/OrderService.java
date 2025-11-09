package com.mikuexpress.mikuexpress.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikuexpress.mikuexpress.dto.CreatePackageDTO;
import com.mikuexpress.mikuexpress.dto.CreatedPackageDTO;
import com.mikuexpress.mikuexpress.dto.StatusUpdateDTO;
import com.mikuexpress.mikuexpress.dto.StatusUpdatedDTO;
import com.mikuexpress.mikuexpress.dto.ViaCepResponseDTO;
import com.mikuexpress.mikuexpress.entity.Order;
import com.mikuexpress.mikuexpress.entity.StatusUpdate;
import com.mikuexpress.mikuexpress.mappers.OrderMapper;
import com.mikuexpress.mikuexpress.mappers.StatusUpdateMapper;
import com.mikuexpress.mikuexpress.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

	private final OrderRepository orderRepository;
	private final ViaCepService viaCepService;
	private final GoogleMapsService googleMapsService;
	private final OrderMapper orderMapper;
	private final EmailService emailService;
	private final StatusUpdateMapper statusUpdateMapper;
	
	/**
	 * Cria um novo pedido/pacote no sistema.
	 * 
	 * Este método realiza o fluxo completo de criação de pedido:
	 * 1. Busca os endereços completos através do ViaCEP usando os CEPs informados
	 * 2. Calcula a distância rodoviária entre origem e destino via Google Maps API
	 * 3. Calcula o preço do frete baseado na distância e peso
	 * 4. Gera um código de rastreio único
	 * 5. Cria o status inicial do pedido
	 * 6. Salva o pedido no banco de dados
	 * 7. Envia e-mail de notificação ao cliente
	 * 
	 * @param dto DTO contendo os dados do pedido (CEPs, dados do cliente, peso)
	 * @return CreatedPackageDTO com os dados do pedido criado incluindo preço e código de rastreio
	 * @throws RuntimeException se os CEPs forem inválidos ou se não for possível calcular a distância
	 */
	@Transactional
	public CreatedPackageDTO createPackage(CreatePackageDTO dto) {
		Order newOrder = orderMapper.toEntity(dto);
		
		log.info("Iniciando processo de criação de pacote para o cliente: {}", dto.customerEmail());

        // --- 2. CHAMAR API VIA CEP ---
        // (Assumindo que dto.getOrigin() e dto.getDestination() são os CEPs)
        log.debug("Buscando endereços no ViaCEP...");
		ViaCepResponseDTO source = viaCepService.getAddressByCep(newOrder.getSource());
		ViaCepResponseDTO destination = viaCepService.getAddressByCep(newOrder.getDestination());
		
		// Validar se os CEPs foram encontrados
		if (!source.isValid() || !destination.isValid()) {
			log.error("CEP inválido: origem válido={}, destino válido={}", source.isValid(), destination.isValid());
			throw new RuntimeException("CEP inválido. Verifique os CEPs informados.");
		}
		
		// --- 3. CHAMAR API GOOGLE MAPS ---
		log.debug("Calculando distância no Google Maps: {} -> {}", source.getAddressForGoogle(), destination.getAddressForGoogle());
		Double distance = googleMapsService.getDistanceInKM(source.getAddressForGoogle(),destination.getAddressForGoogle());
		
		// Validar se a distância foi calculada corretamente
		if (distance == null || distance <= 0) {
			log.error("Distância inválida calculada: {}", distance);
			throw new RuntimeException("Não foi possível calcular a distância entre os endereços.");
		}
		
		// --- 4. CALCULAR O PREÇO ---
		
		newOrder.setDistance(distance.toString());
		Double price = (distance * 0.50) + (newOrder.getWeightInKg() *10.50);
		newOrder.setPrice(price);
		newOrder.setSource(source.getAddressComplete());
		newOrder.setDestination(destination.getAddressComplete());
		newOrder.setDateCreate(LocalDateTime.now());
		newOrder.setTrackingCode(generateTrackingCode());
		
		log.info("Preço final calculado: R$ {} (Distância: {} km, Peso: {} kg, Code: {})", price, distance, dto.weightInKg(), newOrder.getTrackingCode());
		
		// --- 5. CRIAR O STATUS INICIAL ---
		StatusUpdate initStatus = new StatusUpdate();
		initStatus.setStatus("Pedido criado!");
		initStatus.setSource(source.getAddressForGoogle());
		initStatus.setDestination(destination.getAddressForGoogle());
		initStatus.setDateUpdate(LocalDateTime.now());
		initStatus.setOrder(newOrder);
		
		newOrder.setStatusUpdates(new ArrayList<>());
		newOrder.getStatusUpdates().add(initStatus);
		
		//--- 6. SALVAR NO BANCO DE DADOS ---
		orderRepository.save(newOrder);
	
        log.info("Pacote salvo com sucesso. Código: {}", newOrder.getTrackingCode());
		
     // --- 7. ENVIAR E-MAIL DE NOTIFICAÇÃO ---
        try {
        	        	emailService.sendEmailCreate(newOrder);
        	log.info("E-mail de notificação enviado para {}", newOrder.getCustomerEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de notificação para {}", newOrder.getCustomerEmail(), e);
            // Não interrompe o fluxo se o e-mail falhar
        }
		
		return orderMapper.toCreatedDTO(newOrder);
	}
	
	/**
	 * Adiciona uma nova atualização de status a um pedido existente.
	 * 
	 * Este método:
	 * 1. Busca o pedido pelo ID
	 * 2. Cria uma nova atualização de status com a data/hora atual
	 * 3. Associa a atualização ao pedido
	 * 4. Salva o pedido atualizado no banco de dados
	 * 5. Envia e-mail de notificação ao cliente sobre a atualização
	 * 
	 * @param dto DTO contendo o status, localização de origem e destino
	 * @param orderId ID do pedido que será atualizado
	 * @return StatusUpdatedDTO com os dados da atualização incluindo data/hora
	 * @throws RuntimeException se o pedido não for encontrado
	 */
	@Transactional
	public StatusUpdatedDTO addStatusUpdate(StatusUpdateDTO dto, Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new RuntimeException("Pacote não encontrado com ID: " + orderId));
		
		if (order.getStatusUpdates() == null) {
			order.setStatusUpdates(new ArrayList<>());
		}
		
		StatusUpdate status = statusUpdateMapper.toEntity(dto);
		status.setDateUpdate(LocalDateTime.now());
		status.setOrder(order);
		
				order.getStatusUpdates().add(status);
		
		orderRepository.save(order);
		
		// Enviar e-mail após salvar com sucesso
		try {
        	emailService.sendEmailUpdate(order, status);
        	log.info("E-mail de notificação enviado para {}", order.getCustomerEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de notificação para {}", order.getCustomerEmail(), e);
            // Não interrompe o fluxo se o e-mail falhar
        }
		
		return statusUpdateMapper.toUpdatedDTO(status);
	}
	
	/**
	 * Consulta um pedido pelo código de rastreio.
	 * 
	 * Busca um pedido no banco de dados usando o código de rastreio único.
	 * O código de rastreio é gerado automaticamente no formato "MIKUXXXXXXXXBR".
	 * 
	 * @param code Código de rastreio do pedido (ex: "MIKU12345678BR")
	 * @return Order com todos os dados do pedido incluindo histórico de status
	 * @throws RuntimeException se o pedido não for encontrado com o código informado
	 */
	public Order consultByCode(String code) {
		return orderRepository.findByTrackingCode(code)
				.orElseThrow(() -> new RuntimeException("Pacote não encontrado com o codigo: " + code));
	}
	
	/**
	 * Busca um pedido pelo ID.
	 * 
	 * Retorna todos os dados do pedido incluindo o histórico completo de atualizações de status.
	 * 
	 * @param id ID único do pedido no banco de dados
	 * @return Order com todos os dados do pedido
	 * @throws RuntimeException se o pedido não for encontrado com o ID informado
	 */
	public Order getOrderById(Long id) {
		return orderRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Pacote não encontrado com ID: " + id));
	}
	
	/**
	 * Retorna todos os pedidos cadastrados no sistema.
	 * 
	 * Este método é útil para administradores visualizarem todos os pedidos.
	 * Retorna uma lista vazia se não houver pedidos cadastrados.
	 * 
	 * @return Lista de todos os pedidos do sistema
	 */
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}
	
	/**
	 * Busca todos os pedidos de um cliente específico pelo email.
	 * 
	 * Este método é útil para clientes visualizarem apenas seus próprios pedidos.
	 * O email é usado como identificador do cliente.
	 * 
	 * @param customerEmail Email do cliente para filtrar os pedidos
	 * @return Lista de pedidos do cliente informado (pode ser vazia se não houver pedidos)
	 */
	public List<Order> getOrdersByCustomerEmail(String customerEmail) {
		return orderRepository.findByCustomerEmail(customerEmail);
	}
	
	/**
	 * Remove um pedido do sistema permanentemente.
	 * 
	 * Este método exclui o pedido e todas as suas atualizações de status relacionadas
	 * (devido ao CascadeType.ALL na relação OneToMany).
	 * A operação é irreversível.
	 * 
	 * @param id ID do pedido a ser excluído
	 * @throws RuntimeException se o pedido não for encontrado com o ID informado
	 */
	@Transactional
	public void deleteOrder(Long id) {
		if (!orderRepository.existsById(id)) {
			throw new RuntimeException("Pacote não encontrado com ID: " + id);
		}
		orderRepository.deleteById(id);
	}
	
	/**
     * Gera um código de rastreio único para um pedido.
     * 
     * O código é gerado no formato: "MIKU" + 8 caracteres aleatórios (UUID) + "BR"
     * Exemplo: "MIKU12345678BR"
     * 
     * @return String com o código de rastreio único gerado
     */
    private String generateTrackingCode() {
        // Ex: MIKUA4B9C2D8BR
        return "MIKU" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "BR";
    }
}
