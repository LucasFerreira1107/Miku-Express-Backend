package com.mikuexpress.mikuexpress.mappers;

import org.mapstruct.Mapper;

import com.mikuexpress.mikuexpress.dto.CreateUserAdminDTO;
import com.mikuexpress.mikuexpress.entity.UserAdmin;

@Mapper(componentModel="spring")
public interface UserAdminMapper {

	UserAdmin toEntity(CreateUserAdminDTO dto);
	
	CreateUserAdminDTO toDTO(UserAdmin userPublic);
}
