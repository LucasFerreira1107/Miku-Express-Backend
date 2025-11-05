package com.mikuexpress.mikuexpress.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="orders")
public class Order {

	@Id
	@Column(name = "order_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "tracking_code", nullable = false)
	private String trackingCode;
	
	@Column(name = "source", nullable = false)
	private String source;
	
	@Column(name = "destination", nullable = false)
	private String destination;
	
	@Column(name = "distance", nullable = false)
	private String distance;
	
	@Column(name = "price", nullable = false)
	private Double price;
	
	@Column(name = "customer_email", nullable = false)
	private String customerEmail;
	
	@Column(name = "customer_name", nullable = false)
	private String customerName;
	
	@Column(name = "weight_in_kg", nullable = false)
	private Double weightInKg;
	
	@Column(name = "date_create", nullable = false)
	private LocalDateTime dateCreate;
	
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
	private List<StatusUpdate> statusUpdates;
}
