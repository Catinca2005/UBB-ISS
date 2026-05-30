package org.example.weddingplanner.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entity representing a Guest invited to a specific Event.
 * Mapped to the "guests" table in the database.
 */
@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String rsvpStatus;

    @Column
    private String dietaryPreferences;

    @Column
    private String photoFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seating_table_id")
    private SeatingTable seatingTable;

    public Guest() {
        this.id = UUID.randomUUID();
        this.dietaryPreferences = "None"; // Default value as per alternative flow 3.1
        this.rsvpStatus = "Pending";
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

    public String getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(String rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }

    public String getDietaryPreferences() {
        return dietaryPreferences;
    }

    public void setDietaryPreferences(String dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }

    public String getPhotoFilename() {
        return photoFilename;
    }

    public void setPhotoFilename(String photoFilename) {
        this.photoFilename = photoFilename;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public SeatingTable getSeatingTable() {
        return seatingTable;
    }

    public void setSeatingTable(SeatingTable seatingTable) {
        this.seatingTable = seatingTable;
    }
}
