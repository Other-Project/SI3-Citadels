package fr.univ_cotedazur.polytech.si3.team_c.citadels.characters;

public abstract class Character {

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
}
