package com.github.velinyordanov.foodorder.services.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.dto.EnumDto;
import com.github.velinyordanov.foodorder.services.EnumsService;

@Service
public class EnumsServiceImpl implements EnumsService {
    @Override
    public <T extends Enum<T>> Collection<EnumDto> getEnumOptions(Class<T> valueClass) {
	return EnumSet.allOf(valueClass)
		.stream()
		.map(x -> new EnumDto(x.ordinal(), x.name()))
		.collect(Collectors.toList());
    }
}
