package com.mikuexpress.mikuexpress.dto;

import java.util.List;

import com.mikuexpress.mikuexpress.entity.StatusUpdate;

public record CreatedPackageDTO(
		Long id,
		String source,
		String destination,
		String distance,
		Double price,
		String customerEmail,
		String customerName,
		Double weightInKg,
		List<StatusUpdate> statusUpdate) {

}
