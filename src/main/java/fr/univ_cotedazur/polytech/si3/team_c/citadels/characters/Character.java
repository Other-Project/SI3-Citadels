package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

import java.util.Objects;

public abstract class Character implements Comparable<Character> {

    private final String name;
    private final int turn;


    protected Character(String name, int turn) {
        this.name = name;
        this.turn = turn;
    }

    public int getTurn() {
        return this.turn;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int compareTo(Character other) {
        return getTurn() - other.getTurn();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(turn);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Character other)) return false;
        return getTurn() == other.getTurn();
    }
}
