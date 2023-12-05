package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public abstract class Character extends Card implements Comparable<Character> {
    private final int turn;

    protected Character(String name, int turn) {
        this(name, Colors.NONE, turn);
    }

    protected Character(String name, Colors color, int turn) {
        super(name, color);
        this.turn = turn;
    }

    /**
     * @return the turn order number of the character
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * @return the difference between the turn order number of a character and the turn order numbers of another character to compare them
     */
    @Override
    public int compareTo(Character other) {
        return getTurn() - other.getTurn();
    }
}
