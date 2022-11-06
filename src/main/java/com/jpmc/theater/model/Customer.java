package com.jpmc.theater.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Objects;
import java.util.UUID;

@JsonRootName(value = "customer")
public class Customer {

    private String name;
    private int id;

    public Customer() {
        this.id = UUID.randomUUID().hashCode();
    }

    public Customer(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(name, customer.name) && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public String toString() {
        return "name: " + name;
    }
}