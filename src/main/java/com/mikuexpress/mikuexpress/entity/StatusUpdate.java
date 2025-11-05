package com.mikuexpress.mikuexpress.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="status_update")
public class StatusUpdate {

	@Id
	@Column(name = "status_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "status", nullable = false)
	private String status;
	@Column(name = "source", nullable = false)
	private String source;
	
	@Column(name = "destination")
	private String destination;
	
	@Column(name = "date_update", nullable = false)
	private LocalDateTime dateUpdate;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "order_id")
	private Order order;
}
