package com.github.velinyordanov.foodorder.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.velinyordanov.foodorder.dto.DiscountCodeCreateDto;
import com.github.velinyordanov.foodorder.validation.annotations.CompareDates;

@ExtendWith(MockitoExtension.class)
public class CompareDatesValidatorTest {
	private final CompareDatesValidator compareDatesValidator = new CompareDatesValidator();

	@Test
	public void isValidShould_returnTrue_whenBeforeDateIsBeforeAfterDate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setValidFrom(LocalDate.of(2021, Month.JANUARY, 15));
		discountCodeCreateDto.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		CompareDates compareDates = mock(CompareDates.class);
		given(compareDates.before()).willReturn("validFrom");
		given(compareDates.after()).willReturn("validTo");
		
		this.compareDatesValidator.initialize(compareDates);
		assertTrue(this.compareDatesValidator.isValid(discountCodeCreateDto, null));
	}
	
	@Test
	public void isValidShould_returnFalse_whenBeforeDateIsAfterAfterDate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setValidFrom(LocalDate.of(2021, Month.APRIL, 15));
		discountCodeCreateDto.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		CompareDates compareDates = mock(CompareDates.class);
		given(compareDates.before()).willReturn("validFrom");
		given(compareDates.after()).willReturn("validTo");
		
		this.compareDatesValidator.initialize(compareDates);
		assertFalse(this.compareDatesValidator.isValid(discountCodeCreateDto, null));
	}
	
	@Test
	public void isValidShould_returnFalse_whenBeforeDateIsNull()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setValidFrom(null);
		discountCodeCreateDto.setValidTo(LocalDate.of(2021, Month.FEBRUARY, 15));
		
		CompareDates compareDates = mock(CompareDates.class);
		given(compareDates.before()).willReturn("validFrom");
		given(compareDates.after()).willReturn("validTo");
		
		this.compareDatesValidator.initialize(compareDates);
		assertFalse(this.compareDatesValidator.isValid(discountCodeCreateDto, null));
	}
	
	@Test
	public void isValidShould_returnFalse_whenAfterDateIsNull()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setValidFrom(LocalDate.of(2021, Month.APRIL, 15));
		discountCodeCreateDto.setValidTo(null);
		
		CompareDates compareDates = mock(CompareDates.class);
		given(compareDates.before()).willReturn("validFrom");
		given(compareDates.after()).willReturn("validTo");
		
		this.compareDatesValidator.initialize(compareDates);
		assertFalse(this.compareDatesValidator.isValid(discountCodeCreateDto, null));
	}
	
	@Test
	public void isValidShould_returnFalse_whenAfterDateAndBeforeDateAreNull()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DiscountCodeCreateDto discountCodeCreateDto = new DiscountCodeCreateDto();
		discountCodeCreateDto.setValidFrom(null);
		discountCodeCreateDto.setValidTo(null);
		
		CompareDates compareDates = mock(CompareDates.class);
		given(compareDates.before()).willReturn("validFrom");
		given(compareDates.after()).willReturn("validTo");
		
		this.compareDatesValidator.initialize(compareDates);
		assertFalse(this.compareDatesValidator.isValid(discountCodeCreateDto, null));
	}
}
