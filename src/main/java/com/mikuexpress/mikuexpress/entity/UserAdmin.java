package com.mikuexpress.mikuexpress.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="user_admin")
@PrimaryKeyJoinColumn(name = "user_id")
public class UserAdmin extends UserGeneric {

	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "cnpj", nullable = false)
	private String cnpj;
	
	@Column(name = "organization_name", nullable = false)
	private String organizationName;
}
