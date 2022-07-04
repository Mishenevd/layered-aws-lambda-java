package com.mishenev;

import com.amazonaws.util.json.Jackson;

import java.util.Objects;

/**
 * Request for creating a new book.
 *
 * @author Dmitrii_Mishenev
 */
public class CreateBookRequest {

    private String tittle;
    private String name;
    private String description;

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return Jackson.toJsonString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateBookRequest)) {
            return false;
        }
        CreateBookRequest that = (CreateBookRequest) o;
        return Objects.equals(getTittle(), that.getTittle()) && Objects.equals(getName(), that.getName())
                && Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTittle(), getName(), getDescription());
    }
}