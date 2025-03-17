package com.pragati.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pragati.entity.SpendingThresholdRecord;

@Repository
public interface SpendingThresholdRecordRepository extends JpaRepository<SpendingThresholdRecord, Integer> {

	List<SpendingThresholdRecord> findByUserId(UUID userId);

}
