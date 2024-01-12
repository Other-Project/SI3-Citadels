package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.Collections;
import java.util.List;
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

    /**
     * Method to return the special action for a player based on the chosen character
     * or some purple district constructs.
     *
     * @return Empty if the district does not provide additional actions for the player.
     */
    public List<Action> getAction() {
        return Collections.emptyList();
    }

    public List<Action> getEventAction() {
        return Collections.emptyList();
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
        return getColor() + getName() + "\033[0m";
    }
}
