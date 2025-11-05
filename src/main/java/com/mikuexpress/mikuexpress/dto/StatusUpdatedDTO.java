package com.mikuexpress.mikuexpress.dto;

import java.time.LocalDateTime;

public record StatusUpdatedDTO(
		String status,
		String source,
		String destination,
		LocalDateTime dateUpdate ) {

}
