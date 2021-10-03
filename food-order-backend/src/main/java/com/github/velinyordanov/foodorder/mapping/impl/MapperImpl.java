package com.github.velinyordanov.foodorder.mapping.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.velinyordanov.foodorder.mapping.Mapper;

@Component
public class MapperImpl implements Mapper {
	private final ModelMapper modelMapper;

	public MapperImpl(ModelMapper mapper) {
		this.modelMapper = mapper;
	}

	@Override
	public <TDest> TDest map(Object source, Class<TDest> destination) {
		return this.modelMapper.map(source, destination);
	}

	@Override
	public void map(Object source, Object destination) {
		this.modelMapper.map(source, destination);
	}
}
