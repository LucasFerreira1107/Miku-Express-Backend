package com.mikuexpress.mikuexpress.mappers;

import org.mapstruct.Mapper;

import com.mikuexpress.mikuexpress.dto.StatusUpdateDTO;
import com.mikuexpress.mikuexpress.dto.StatusUpdatedDTO;
import com.mikuexpress.mikuexpress.entity.StatusUpdate;

@Mapper(componentModel="spring")
public interface StatusUpdateMapper {

	StatusUpdate toEntity(StatusUpdateDTO dto);
	
	StatusUpdatedDTO toUpdatedDTO(StatusUpdate statusUpdated);
}
