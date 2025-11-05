package com.mikuexpress.mikuexpress.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mikuexpress.mikuexpress.entity.Order;
import com.mikuexpress.mikuexpress.entity.StatusUpdate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;
	
	/**
	 * Envia e-mail de notificação ao cliente quando um pedido é criado.
	 * 
	 * O e-mail contém:
	 * - Mensagem de boas-vindas personalizada com o nome do cliente
	 * - Código de rastreio do pedido
	 * - Status inicial do pedido
	 * 
	 * @param order Pedido criado para o qual o e-mail será enviado
	 * @throws RuntimeException se houver falha ao enviar o e-mail
	 */
	public void sendEmailCreate(Order order) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(order.getCustomerEmail());
			message.setSubject("Miku Express: Pedido Criado!");
			
			String statusTexto = "Pedido criado!";
			if (order.getStatusUpdates() != null 
					&& !order.getStatusUpdates().isEmpty() 
					&& order.getStatusUpdates().get(0) != null) {
				statusTexto = order.getStatusUpdates().get(0).getStatus();
			}
			
			message.setText("Olá, " + order.getCustomerName() + "!\n\n" 
					+ "Seu pedido foi criado com sucesso. \n"
					+ "Código de Rastreio: " + order.getTrackingCode()
					+ "\n"
					+ "Status atual: " + statusTexto);
			
			mailSender.send(message);
			log.info("E-mail de criação de pedido enviado para {}", order.getCustomerEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de criação para {}", order.getCustomerEmail(), e);
			throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Envia e-mail de notificação ao cliente quando há uma atualização de status no pedido.
	 * 
	 * O e-mail contém:
	 * - Mensagem personalizada com o nome do cliente
	 * - Código de rastreio do pedido
	 * - Novo status do pedido
	 * - Localização atual (origem) e destino (se informado)
	 * 
	 * @param order Pedido que teve o status atualizado
	 * @param statusUpdate Objeto contendo os dados da atualização de status
	 * @throws RuntimeException se houver falha ao enviar o e-mail
	 */
	public void sendEmailUpdate(Order order, StatusUpdate statusUpdate) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(order.getCustomerEmail());
			message.setSubject("Miku Express: Atualização do Pedido " + order.getTrackingCode());
			
			String source = statusUpdate.getSource() != null ? statusUpdate.getSource() : "Não informada";
			String destination = statusUpdate.getDestination() != null ? statusUpdate.getDestination() : "Não informada";
			
			message.setText("Olá, " + order.getCustomerName() + "!\n\n" +
					"Seu pedido teve uma nova atualização:\n" +
					"Status: " + statusUpdate.getStatus() + "\n" +
					"Origem: " + source + "\n"+
					"Destino: "+ destination);
			
			mailSender.send(message);
			log.info("E-mail de atualização enviado para {}", order.getCustomerEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de atualização para {}", order.getCustomerEmail(), e);
			throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
		}
	}
}
