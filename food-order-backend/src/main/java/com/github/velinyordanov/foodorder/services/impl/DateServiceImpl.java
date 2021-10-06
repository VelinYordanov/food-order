package com.github.velinyordanov.foodorder.services.impl;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
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

	@Override
	public String getMonthName(int month) {
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	@Override
	public int getNumberOfDaysForMonth(int year, int month) {
		YearMonth yearMonthObject = YearMonth.of(year, month);
		return yearMonthObject.lengthOfMonth();
	}
}
