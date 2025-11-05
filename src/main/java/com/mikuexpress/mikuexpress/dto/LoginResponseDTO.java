package com.mikuexpress.mikuexpress.dto;

import com.mikuexpress.mikuexpress.enums.Role;

public record LoginResponseDTO(
		String token,
		String tokenType,
		String email,
		String name,
		Role role,
		Long userId
) {
	public static LoginResponseDTO of(String token, String email, String name, Role role, Long userId) {
		return new LoginResponseDTO(token, "Bearer", email, name, role, userId);
	}
}
