package org.example.weddingplanner.repository;

import org.example.weddingplanner.model.SeatingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatingTableRepository extends JpaRepository<SeatingTable, UUID> {
    List<SeatingTable> findByEventId(UUID eventId);
    long countByEventId(UUID eventId);
}
