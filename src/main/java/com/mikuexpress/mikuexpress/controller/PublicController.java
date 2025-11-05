package com.mikuexpress.mikuexpress.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mikuexpress.mikuexpress.entity.Order;
import com.mikuexpress.mikuexpress.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publics")
@RequiredArgsConstructor
public class PublicController {

	private final OrderService orderService;
	
	/**
	 * Consulta um pedido pelo código de rastreio (endpoint público).
	 * 
	 * Este é um endpoint público que não requer autenticação, permitindo que
	 * qualquer pessoa consulte o status de um pedido usando apenas o código de rastreio.
	 * 
	 * @param code Código de rastreio do pedido (ex: "MIKU12345678BR")
	 * @return ResponseEntity com status 200 (OK) e os dados do pedido incluindo histórico de status
	 */
	@GetMapping("/orders/tracking")
	public ResponseEntity<Order> getOrderByTrackingCode(@RequestParam String code) {
		return ResponseEntity.ok(orderService.consultByCode(code));
	}
}
