package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.Objects;

public abstract class Card {
    private final String name;
    private final Colors color;

    protected Card(String name, Colors color) {
        this.name = name;
        this.color = color;
    }

    /**
     * @return the color of the card
     */
    public Colors getColor() {
        return color;
    }

    /**
     * @return the name of the card
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Card other)) return false;
        return Objects.equals(getName(), other.getName());
    }

    @Override
    public String toString() {
        return getColor() + getName();
    }
}
