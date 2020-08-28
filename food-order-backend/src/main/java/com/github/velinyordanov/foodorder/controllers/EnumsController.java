package com.github.velinyordanov.foodorder.controllers;

import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.velinyordanov.foodorder.dto.EnumDto;
import com.github.velinyordanov.foodorder.enums.AddressType;
import com.github.velinyordanov.foodorder.services.EnumsService;

@RestController
@RequestMapping("enums")
@PreAuthorize("isAuthenticated()")
public class EnumsController {
    private final EnumsService enumsService;

    public EnumsController(EnumsService enumsService) {
	this.enumsService = enumsService;
    }

    @GetMapping("address-types")
    public Collection<EnumDto> getAddressTypes() {
	return this.enumsService.getEnumOptions(AddressType.class);
    }
}
