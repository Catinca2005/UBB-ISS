package org.example.weddingplanner.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "shopping_items")
public class ShoppingItem {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String itemName;

    @Column
    private String category; // e.g., "Decoratiuni", "Marturii"

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private Boolean isPurchased;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public ShoppingItem() {
        this.id = UUID.randomUUID();
        this.isPurchased = false;
        this.cost = 0.0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Boolean getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(Boolean purchased) {
        isPurchased = purchased;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
