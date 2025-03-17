package com.pragati.service;

import org.springframework.stereotype.Service;

import com.pragati.repository.NoteTagMappingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NoteTagMappingService {

	private final NoteTagMappingRepository noteTagMappingRepository;

}
