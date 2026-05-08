package org.example.weddingplanner.repository;

import org.example.weddingplanner.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object (DAO) for the Event entity.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Retrieves a list of all events managed by a specific organizer.
     * Spring Data JPA automatically implements this query based on the method name.
     *
     * @param organizerId The UUID of the user (organizer).
     * @return A list of events belonging to the specified user.
     */
    List<Event> findAllByOrganizerId(UUID organizerId);

}