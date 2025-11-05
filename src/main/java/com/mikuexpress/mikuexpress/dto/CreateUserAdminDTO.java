package com.mikuexpress.mikuexpress.dto;

public record CreateUserAdminDTO(
		String name,
		String email,
		String password,
		String phoneNumber,
		String cnpj,
		String organizationName) {

}
