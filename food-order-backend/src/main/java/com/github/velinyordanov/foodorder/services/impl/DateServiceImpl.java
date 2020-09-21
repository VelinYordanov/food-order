package com.github.velinyordanov.foodorder.services.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.github.velinyordanov.foodorder.services.DateService;

@Service
public class DateServiceImpl implements DateService {
    @Override
    public LocalDate now() {
	return LocalDate.now();
    }

    @Override
    public LocalDate utcNow() {
	return LocalDate.now(ZoneOffset.UTC);
    }

    @Override
    public boolean isInThePast(LocalDate date) {
	return date.isBefore(this.utcNow());
    }

    @Override
    public boolean isInTheFuture(LocalDate date) {
	return date.isAfter(this.utcNow());
    }
}
