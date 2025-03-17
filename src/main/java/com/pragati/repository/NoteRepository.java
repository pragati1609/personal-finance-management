package com.pragati.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pragati.entity.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

}
