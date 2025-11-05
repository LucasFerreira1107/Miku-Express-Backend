package com.mikuexpress.mikuexpress.mappers;

import org.mapstruct.Mapper;

import com.mikuexpress.mikuexpress.dto.CreateUserPublicDTO;
import com.mikuexpress.mikuexpress.entity.UserPublic;

@Mapper(componentModel="spring")
public interface UserPublicMapper {

	UserPublic toEntity(CreateUserPublicDTO dto);
	
	CreateUserPublicDTO toDTO(UserPublic userPublic);
}
