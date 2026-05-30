package org.example.weddingplanner.repository;

import org.example.weddingplanner.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {
    List<Guest> findByEventId(UUID eventId);
    List<Guest> findBySeatingTableId(UUID tableId);
    List<Guest> findByEventIdAndSeatingTableIsNull(UUID eventId);
    long countBySeatingTableId(UUID tableId);
}
