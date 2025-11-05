package com.mikuexpress.mikuexpress.dto;

import java.time.LocalDate;

import com.mikuexpress.mikuexpress.enums.Gender;

public record UpdateUserPublicDTO(
		String name,
		String phoneNumber,
		LocalDate dateOfBirth,
		Gender gender) {

}
