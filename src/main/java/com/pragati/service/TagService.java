package com.pragati.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pragati.constant.ContextType;
import com.pragati.dto.response.TagRetreivalRequestDto;
import com.pragati.entity.Tag;
import com.pragati.repository.NoteTagMappingRepository;
import com.pragati.repository.TagRepository;
import com.pragati.repository.TicketTagMappingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TagService {

	private final TagRepository tagRepository;

	private final NoteTagMappingRepository noteTagMappingRepository;

	private final TicketTagMappingRepository ticketTagMappingRepository;

	public List<Tag> retreiveAll() {
		return tagRepository.findAll();
	}

	public ResponseEntity<List<String>> retreive(final TagRetreivalRequestDto tagRetreivalRequest, final String token) {
		if (tagRetreivalRequest.getContextType().equalsIgnoreCase(ContextType.NOTE.getName())) {
			return ResponseEntity
					.ok(noteTagMappingRepository.findByNoteId(tagRetreivalRequest.getContextId()).parallelStream()
							.map(noteTagMapping -> noteTagMapping.getTag().getName()).collect(Collectors.toList()));
		}
		if (tagRetreivalRequest.getContextType().equalsIgnoreCase(ContextType.COMPLETED_TICKET.getName())
				|| tagRetreivalRequest.getContextType().equalsIgnoreCase(ContextType.FUTURE_TICKET.getName()))
			return ResponseEntity
					.ok(ticketTagMappingRepository.findByTicketId(tagRetreivalRequest.getContextId()).parallelStream()
							.map(ticketTagMapping -> ticketTagMapping.getTag().getName()).collect(Collectors.toList()));
		return ResponseEntity.badRequest().build();

	}

}
