package com.github.velinyordanov.foodorder.services;

import java.util.Collection;

import com.github.velinyordanov.foodorder.dto.EnumDto;

public interface EnumsService {
	<T extends Enum<T>> Collection<EnumDto> getEnumOptions(Class<T> valueClass);
}
