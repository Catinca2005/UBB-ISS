package org.example.weddingplanner.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a planned event
 * Mapped to the "events" table in the database.
 */
@Entity
@Table(name = "events")
public class Event {

    /**
     * Unique identifier for the event.
     * Utilizes UUID to prevent URL parameter manipulation (ID-OR vulnerabilities).
     */
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * The official name of the event (e.g., "Andrei & Catinca's Wedding").
     */
    @Column(nullable = false)
    private String name;

    /**
     * The scheduled date for the event.
     * Using LocalDate for modern, timezone-safe date handling.
     */
    private LocalDate eventDate;

    /**
     * The total allocated budget for this specific event.
     */
    private Double totalBudget;

    /**
     * The user who owns and manages this event.
     * Defines a Many-To-One relationship: multiple events can belong to one user.
     * FetchType.LAZY is used for performance optimization (only loads the user when explicitly requested).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User organizer;

    /**
     * Default constructor required by JPA.
     * Automatically generates a unique UUID upon object creation.
     */
    public Event() {
        this.id = UUID.randomUUID();
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }
}