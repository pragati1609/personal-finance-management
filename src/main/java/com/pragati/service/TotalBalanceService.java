package com.pragati.service;

import org.springframework.stereotype.Service;

import com.pragati.repository.TotalBalanceRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TotalBalanceService {

	private final TotalBalanceRepository totalBalanceRepository;

}
