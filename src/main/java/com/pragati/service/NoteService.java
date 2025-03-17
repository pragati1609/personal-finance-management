package com.pragati.service;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pragati.dto.request.NoteCreationRequestDto;
import com.pragati.dto.request.NoteUpdationRequestDto;
import com.pragati.dto.response.NoteDto;
import com.pragati.entity.Note;
import com.pragati.entity.NoteTagMapping;
import com.pragati.entity.Tag;
import com.pragati.repository.NoteRepository;
import com.pragati.repository.NoteTagMappingRepository;
import com.pragati.repository.TagRepository;
import com.pragati.repository.UserRepository;
import com.pragati.security.utility.JwtUtils;
import com.pragati.utils.ResponseUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NoteService {

	private final NoteRepository noteRepository;

	private final UserRepository userRepository;

	private final TagRepository tagRepository;

	private final NoteTagMappingRepository noteTagMappingRepository;

	private final JwtUtils jwtUtils;

	private final ResponseUtils responseUtils;

	public ResponseEntity<?> create(final NoteCreationRequestDto noteCreationRequest, final String token) {
		final var note = new Note();
		note.setActive(true);
		note.setDescription(noteCreationRequest.getDescription());
		note.setTitle(noteCreationRequest.getTitle());
		note.setUserId(jwtUtils.extractUserId(token.replace("Bearer ", "")));

		final var savedNote = noteRepository.save(note);

		noteCreationRequest.getTags().forEach(noteTag -> {
			final var tag = tagRepository.findByName(noteTag.getName().toUpperCase()).orElse(new Tag());
			tag.setName(noteTag.getName().toUpperCase());
			final var savedTag = tagRepository.save(tag);

			final var noteTagMapping = new NoteTagMapping();
			noteTagMapping.setTagId(savedTag.getId());
			noteTagMapping.setNoteId(savedNote.getId());
			noteTagMappingRepository.save(noteTagMapping);
		});

		return responseUtils.noteSuccessResponse(savedNote.getId());
	}

	public ResponseEntity<?> update(final NoteUpdationRequestDto noteUpdationRequest, final String token) {
		final var note = noteRepository.findById(noteUpdationRequest.getId()).get();
		note.setActive(noteUpdationRequest.getIsActive());
		note.setDescription(noteUpdationRequest.getDescription());

		final var savedNote = noteRepository.save(note);

		noteUpdationRequest.getTags().forEach(noteTag -> {
			final var tag = tagRepository.findByName(noteTag.getName().toUpperCase()).orElse(new Tag());
			tag.setName(noteTag.getName().toUpperCase());
			final var savedTag = tagRepository.save(tag);

			final var noteTagMapping = noteTagMappingRepository
					.findByTagIdAndNoteId(savedTag.getId(), savedNote.getId()).orElse(new NoteTagMapping());
			noteTagMapping.setTagId(savedTag.getId());
			noteTagMapping.setNoteId(savedNote.getId());
			noteTagMappingRepository.save(noteTagMapping);
		});

		return responseUtils.noteSuccessResponse(savedNote.getId());
	}

	public ResponseEntity<?> retreive(final String token) {
		final var user = userRepository.findById(jwtUtils.extractUserId(token.replace("Bearer ", ""))).get();
		return ResponseEntity.ok(user.getNotes().parallelStream()
				.map(note -> NoteDto.builder().id(note.getId()).createdAt(note.getCreatedAt())
						.description(note.getDescription()).isActive(note.isActive()).title(note.getTitle())
						.updatedAt(note.getUpdatedAt()).build())
				.collect(Collectors.toList()));
	}

}
