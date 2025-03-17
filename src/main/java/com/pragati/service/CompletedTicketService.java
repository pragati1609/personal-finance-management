package com.pragati.service;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pragati.dto.request.CompletedTicketCreationRequestDto;
import com.pragati.dto.response.BalanceModeDto;
import com.pragati.dto.response.TicketDto;
import com.pragati.entity.CompletedTicket;
import com.pragati.entity.Tag;
import com.pragati.entity.TicketTagMapping;
import com.pragati.repository.CompletedTicketRepository;
import com.pragati.repository.TagRepository;
import com.pragati.repository.TicketTagMappingRepository;
import com.pragati.repository.UserRepository;
import com.pragati.security.utility.JwtUtils;
import com.pragati.utils.ResponseUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompletedTicketService {

	private final CompletedTicketRepository completedTicketRepository;

	private final TagRepository tagRepository;

	private final TicketTagMappingRepository ticketTagMappingRepository;

	private final UserRepository userRepository;

	private final JwtUtils jwtUtils;

	private final ResponseUtils responseUtils;

	public ResponseEntity<?> create(final CompletedTicketCreationRequestDto completedTicketCreationRequest,
			final String token) {
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));
		final var completedTicket = new CompletedTicket();

		completedTicket.setBalanceModeId(completedTicketCreationRequest.getBalanceModeId());
		completedTicket.setDescription(completedTicketCreationRequest.getDescription());
		completedTicket.setName(completedTicketCreationRequest.getName());
		completedTicket.setTicketCompletionDate(completedTicketCreationRequest.getTicketCompletionDate());
		completedTicket.setValue(completedTicketCreationRequest.getValue());
		completedTicket.setTicketType(completedTicketCreationRequest.getTicketType());
		completedTicket.setUserId(userId);

		final var savedCompletedTicket = completedTicketRepository.save(completedTicket);

		completedTicketCreationRequest.getTags().forEach(ticketTag -> {
			System.out.println("boom " + ticketTag.getName());
			final var tag = tagRepository.findByName(ticketTag.getName().toUpperCase()).orElse(new Tag());
			tag.setName(ticketTag.getName().toUpperCase());
			final var savedTag = tagRepository.save(tag);

			final var ticketTagMapping = new TicketTagMapping();
			ticketTagMapping.setTagId(savedTag.getId());
			ticketTagMapping.setTicketId(savedCompletedTicket.getId());
			ticketTagMapping.setTicketType("COMPLETED");

			ticketTagMappingRepository.save(ticketTagMapping);

		});

		return responseUtils.completedTicketSuccessResponse(savedCompletedTicket.getId());
	}

	public ResponseEntity<?> delete(final UUID ticketId, final String token) {
		final var completedTicketId = completedTicketRepository.findById(ticketId).get();
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));

		if (!completedTicketId.getUserId().equals(userId))
			responseUtils.unauthorizedResponse();

		completedTicketRepository.deleteById(ticketId);
		return responseUtils.completedTicketDeletionResponse();
	}

	public ResponseEntity<?> retreiveExpenses(final String token) {
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));
		final var user = userRepository.findById(userId).get();
		return ResponseEntity.ok(user.getCompletedTickets().parallelStream()
				.filter(completedTicket -> completedTicket.getTicketType().equalsIgnoreCase("expense"))
				.map(completedTicket -> {
					final var balanceMode = completedTicket.getBalanceMode();
					final var balanceModeDto = BalanceModeDto.builder().createdAt(balanceMode.getCreatedAt())
							.id(balanceMode.getId()).isActive(balanceMode.isActive())
							.modeType(balanceMode.getModeType()).name(balanceMode.getName())
							.updatedAt(balanceMode.getUpdatedAt()).value(balanceMode.getValue()).build();
					return TicketDto.builder().createdAt(completedTicket.getCreatedAt())
							.description(completedTicket.getDescription()).id(completedTicket.getId())
							.name(completedTicket.getName())
							.ticketCompletionDate(completedTicket.getTicketCompletionDate())
							.updatedAt(completedTicket.getUpdatedAt()).value(completedTicket.getValue())
							.ticketType1("COMPLETED").ticketType2(completedTicket.getTicketType())
							.balanceMode(balanceModeDto).build();
				}).collect(Collectors.toList()));
	}

	public ResponseEntity<?> retreiveGains(final String token) {
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));
		final var user = userRepository.findById(userId).get();
		return ResponseEntity.ok(user.getCompletedTickets().parallelStream()
				.filter(completedTicket -> completedTicket.getTicketType().equalsIgnoreCase("gain"))
				.map(completedTicket -> {
					final var balanceMode = completedTicket.getBalanceMode();
					final var balanceModeDto = BalanceModeDto.builder().createdAt(balanceMode.getCreatedAt())
							.id(balanceMode.getId()).isActive(balanceMode.isActive())
							.modeType(balanceMode.getModeType()).name(balanceMode.getName())
							.updatedAt(balanceMode.getUpdatedAt()).value(balanceMode.getValue()).build();
					return TicketDto.builder().createdAt(completedTicket.getCreatedAt())
							.description(completedTicket.getDescription()).id(completedTicket.getId())
							.name(completedTicket.getName())
							.ticketCompletionDate(completedTicket.getTicketCompletionDate())
							.updatedAt(completedTicket.getUpdatedAt()).value(completedTicket.getValue())
							.ticketType1("COMPLETED").ticketType2(completedTicket.getTicketType())
							.balanceMode(balanceModeDto).build();
				}).collect(Collectors.toList()));
	}

}
