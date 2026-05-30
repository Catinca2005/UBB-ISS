package org.example.weddingplanner.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a Task for a specific Event.
 * Mapped to the "tasks" table in the database.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column
    private String role; // e.g., "Fotograf"

    @Column
    private String assigneeName; // e.g., "Ion Popescu"

    @Column
    private LocalDate deadline;

    @Column(nullable = false)
    private Double estimatedCost;

    @Column(nullable = false)
    private String status; // "Pending" or "Completed"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Task() {
        this.id = UUID.randomUUID();
        this.status = "Pending";
        this.estimatedCost = 0.0;
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
