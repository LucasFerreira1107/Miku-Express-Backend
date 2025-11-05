package com.mikuexpress.mikuexpress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikuexpress.mikuexpress.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByTrackingCode(String code);
	
	List<Order> findByCustomerEmail(String customerEmail);
}
