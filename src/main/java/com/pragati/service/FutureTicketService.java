package com.pragati.service;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pragati.dto.request.FutureTicketCreationRequestDto;
import com.pragati.dto.response.BalanceModeDto;
import com.pragati.dto.response.TicketDto;
import com.pragati.entity.FutureTicket;
import com.pragati.entity.Tag;
import com.pragati.entity.TicketTagMapping;
import com.pragati.repository.FutureTicketRepository;
import com.pragati.repository.TagRepository;
import com.pragati.repository.TicketTagMappingRepository;
import com.pragati.repository.UserRepository;
import com.pragati.security.utility.JwtUtils;
import com.pragati.utils.ResponseUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FutureTicketService {

	private final FutureTicketRepository futureTicketRepository;

	private final UserRepository userRepository;

	private final TagRepository tagRepository;

	private final TicketTagMappingRepository ticketTagMappingRepository;

	private final JwtUtils jwtUtils;

	private final ResponseUtils responseUtils;

	public ResponseEntity<?> create(final FutureTicketCreationRequestDto futureTicketCreationRequest,
			final String token) {
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));
		final var futureTicket = new FutureTicket();

		futureTicket.setBalanceModeId(futureTicketCreationRequest.getBalanceModeId());
		futureTicket.setDescription(futureTicketCreationRequest.getDescription());
		futureTicket.setName(futureTicketCreationRequest.getName());
		if (futureTicketCreationRequest.getTicketCompletionDate() != null)
			futureTicket.setTicketCompletionDate(futureTicketCreationRequest.getTicketCompletionDate());
		futureTicket.setValue(futureTicketCreationRequest.getValue());
		futureTicket.setTicketType(futureTicketCreationRequest.getTicketType());
		futureTicket.setUserId(userId);

		final var savedFutureTicket = futureTicketRepository.save(futureTicket);

		futureTicketCreationRequest.getTags().forEach(ticketTag -> {
			final var tag = tagRepository.findByName(ticketTag.getName().toUpperCase()).orElse(new Tag());
			tag.setName(ticketTag.getName().toUpperCase());
			final var savedTag = tagRepository.save(tag);

			final var ticketTagMapping = new TicketTagMapping();
			ticketTagMapping.setTagId(savedTag.getId());
			ticketTagMapping.setTicketId(savedFutureTicket.getId());
			ticketTagMapping.setTicketType("FUTURE");

			ticketTagMappingRepository.save(ticketTagMapping);

		});

		return responseUtils.futureTicketSuccessResponse(savedFutureTicket.getId());
	}

	public ResponseEntity<?> delete(final UUID ticketId, final String token) {
		final var futureTicketId = futureTicketRepository.findById(ticketId).get();
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));

		if (!futureTicketId.getUserId().equals(userId))
			responseUtils.unauthorizedResponse();

		futureTicketRepository.deleteById(ticketId);
		return responseUtils.futureTicketDeletionResponse();
	}

	public ResponseEntity<?> retreiveExpenses(final String token) {
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));
		final var user = userRepository.findById(userId).get();
		return ResponseEntity.ok(user.getFutureTickets().parallelStream()
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
							.ticketType1("FUTURE").ticketType2(completedTicket.getTicketType())
							.balanceMode(balanceModeDto).build();
				}).collect(Collectors.toList()));
	}

	public ResponseEntity<?> retreiveGains(final String token) {
		final var userId = jwtUtils.extractUserId(token.replace("Bearer ", ""));
		final var user = userRepository.findById(userId).get();
		return ResponseEntity.ok(user.getFutureTickets().parallelStream()
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
							.ticketType1("FUTURE").ticketType2(completedTicket.getTicketType())
							.balanceMode(balanceModeDto).build();
				}).collect(Collectors.toList()));
	}

}
