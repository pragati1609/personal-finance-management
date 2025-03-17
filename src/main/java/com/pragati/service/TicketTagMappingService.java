package com.pragati.service;

import org.springframework.stereotype.Service;

import com.pragati.repository.TicketTagMappingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TicketTagMappingService {

	private final TicketTagMappingRepository ticketTagMappingRepository;

}
