package com.mikuexpress.mikuexpress.entity;

import java.time.LocalDate;

import com.mikuexpress.mikuexpress.enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="user_public")
@PrimaryKeyJoinColumn(name = "user_id")
public class UserPublic extends UserGeneric{

	private static final long serialVersionUID = 1L;

	@Column(name = "cpf", nullable = false)
	private String cpf;
	
	@Column(name = "date_of_birth", nullable = false)
	private LocalDate dateOfBirth;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "gender", nullable = false)
	private Gender gender;
}
