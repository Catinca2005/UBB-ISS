package org.example.weddingplanner.repository;

import org.example.weddingplanner.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByEventId(UUID eventId);

    // Calculates the total spent for an event quickly in the database
    @Query("SELECT COALESCE(SUM(t.estimatedCost), 0.0) FROM Task t WHERE t.event.id = :eventId")
    Double sumEstimatedCostByEventId(@Param("eventId") UUID eventId);
}
