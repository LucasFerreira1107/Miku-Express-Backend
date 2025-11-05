package com.mikuexpress.mikuexpress.dto;

import java.time.LocalDate;

import com.mikuexpress.mikuexpress.enums.Gender;

public record CreateUserPublicDTO(
		String name,
		String email,
		String password,
		String phoneNumber,
		String cpf,
		LocalDate dateOfBirth,
		Gender gender) {

}
