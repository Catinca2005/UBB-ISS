package org.example.weddingplanner.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "seating_tables")
public class SeatingTable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String tableName;

    @Column(nullable = false)
    private Integer maxSeats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public SeatingTable() {
        this.id = UUID.randomUUID();
        this.maxSeats = 10;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public Integer getMaxSeats() { return maxSeats; }
    public void setMaxSeats(Integer maxSeats) { this.maxSeats = maxSeats; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
