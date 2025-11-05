package com.mikuexpress.mikuexpress.mappers;

import org.mapstruct.Mapper;

import com.mikuexpress.mikuexpress.dto.CreatePackageDTO;
import com.mikuexpress.mikuexpress.dto.CreatedPackageDTO;
import com.mikuexpress.mikuexpress.entity.Order;

@Mapper(componentModel="spring")
public interface OrderMapper {

	Order toEntity(CreatePackageDTO dto);
	
	CreatePackageDTO toDTO(Order order);
	CreatedPackageDTO toCreatedDTO(Order order);
	
}
