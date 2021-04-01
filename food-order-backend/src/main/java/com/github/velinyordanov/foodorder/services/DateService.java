package com.github.velinyordanov.foodorder.services;

import java.time.LocalDate;

public interface DateService {
	LocalDate now();

	LocalDate utcNow();

	boolean isInThePast(LocalDate date);

	boolean isInTheFuture(LocalDate date);

	String getMonthName(int month);

	int getNumberOfDaysForMonth(int year, int month);
}
