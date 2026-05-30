package org.example.weddingplanner.repository;

import org.example.weddingplanner.model.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, UUID> {
    
    List<ShoppingItem> findByEventId(UUID eventId);

    @Query("SELECT COALESCE(SUM(s.cost), 0.0) FROM ShoppingItem s WHERE s.event.id = :eventId")
    Double sumCostByEventId(@Param("eventId") UUID eventId);
}
