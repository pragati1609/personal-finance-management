package com.pragati.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pragati.dto.request.MonthlySpendingThresholdLimitRequestDto;
import com.pragati.repository.CurrentMonthlySpendingThresholdLimitRepository;
import com.pragati.security.utility.JwtUtils;
import com.pragati.utils.ResponseUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CurrentMonthlySpendingThresholdLimitService {

	private final CurrentMonthlySpendingThresholdLimitRepository currentMonthlySpendingThresholdLimitRepository;
	private final JwtUtils jwtUtils;
	private final ResponseUtils responseUtils;

	public ResponseEntity<?> update(final MonthlySpendingThresholdLimitRequestDto monthlySpendingThresholdLimitRequest,
			final String token) {
		final var currentMonthlySpendingThresholdLimit = currentMonthlySpendingThresholdLimitRepository
				.findByUserId(jwtUtils.extractUserId(token.replace("Bearer ", ""))).get();

		currentMonthlySpendingThresholdLimit.setIsActive(monthlySpendingThresholdLimitRequest.getActive());
		currentMonthlySpendingThresholdLimit.setLimitValue(monthlySpendingThresholdLimitRequest.getLimitValue());

		currentMonthlySpendingThresholdLimitRepository.save(currentMonthlySpendingThresholdLimit);
		return responseUtils.monthlySpendingUpdationSuccessResponse();
	}

}
