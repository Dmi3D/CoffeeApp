package com.example.consumer;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id;
    private String customerName;
    private String coffeeType;
    private String milkType;
    private int numShots;
    private List<String> syrups;
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCoffeeType() { return coffeeType; }
    public void setCoffeeType(String coffeeType) { this.coffeeType = coffeeType; }

    public String getMilkType() { return milkType; }
    public void setMilkType(String milkType) { this.milkType = milkType; }

    public int getNumShots() { return numShots; }
    public void setNumShots(int numShots) { this.numShots = numShots; }

    public List<String> getSyrups() { return syrups; }
    public void setSyrups(List<String> syrups) { this.syrups = syrups; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
