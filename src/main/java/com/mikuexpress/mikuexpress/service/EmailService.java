package com.mikuexpress.mikuexpress.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.mikuexpress.mikuexpress.entity.Order;
import com.mikuexpress.mikuexpress.entity.StatusUpdate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
	
	private static final String LOGO_CID = "logoMikuExpress";
    private static final String LOGO_PATH = "imagens/logo.png";
	
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
			
			//Contexto do Thymeleaf
			Context context = new Context();
			context.setVariable("nomeCliente", order.getCustomerName());
			context.setVariable("codigoRastreio", order.getTrackingCode());
			context.setVariable("logoCid", LOGO_CID);
			
			String statusTexto = "Pedido criado!";

			if (order.getStatusUpdates() != null 
					&& !order.getStatusUpdates().isEmpty() 
					&& order.getStatusUpdates().get(0) != null) {
				statusTexto = order.getStatusUpdates().get(0).getStatus();
			}
			context.setVariable("statusInicial", statusTexto);;

			//Processar o template HTML
			String htmlContent = templateEngine.process("pedido-criado", context);
			
			//Enviar Email
			sendHtmlEmail(order.getCustomerEmail(), "Miku Express: Pedido Criado!", htmlContent, LOGO_PATH, LOGO_CID);
			
			log.info("E-mail de criação de pedido enviado para {}", order.getCustomerEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de criação para {}", order.getCustomerEmail(), e);
			throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
		}
	}
	
	private void sendHtmlEmail(String to, String subject, 
			String htmlContent, String resourcePath, String resourceCid) throws MessagingException{
		//MimeMessage para envio do html
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
		
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);
		
		//Anexa a imagem
		ClassPathResource resource = new ClassPathResource(resourcePath);
		helper.addInline(resourceCid, resource);
		mailSender.send(mimeMessage);
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
			
			Context context = new Context();
			context.setVariable("nomeCliente", order.getCustomerName());
			context.setVariable("codigoRastreio", order.getTrackingCode());
			context.setVariable("novoStatus", statusUpdate.getStatus());
			context.setVariable("origem", statusUpdate.getSource());
			context.setVariable("destino", statusUpdate.getDestination());
			
			context.setVariable("logoCid", LOGO_CID);
			
			String htmlContent = templateEngine.process("atualizacao-status", context);
			
			sendHtmlEmail(order.getCustomerEmail(), "Miku Express: Atualização do Pedido " + order.getTrackingCode()
			, htmlContent,LOGO_PATH, LOGO_CID);
			
			log.info("E-mail de atualização enviado para {}", order.getCustomerEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de atualização para {}", order.getCustomerEmail(), e);
			throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
		}
	}
}
