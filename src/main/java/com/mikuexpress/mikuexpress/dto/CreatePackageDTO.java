package com.mikuexpress.mikuexpress.dto;

public record CreatePackageDTO(
		String source,
		String destination,
		String customerEmail,
		String customerName,
		Double weightInKg) {

}
