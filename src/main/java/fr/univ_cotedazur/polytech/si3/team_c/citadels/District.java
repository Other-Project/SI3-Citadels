package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public abstract class District extends Card {
    private final int cost;
    private final int point;

    protected District(String name, int cost, Colors color, int point) {
        super(name, color);
        this.cost = cost;
        this.point = point;
    }

    public int getCost() {
        return cost;
    }

    public int getPoint() {
        return point;
    }

    public boolean isDestructible() {
        return true;
    }

    public int numberOfCardsToDraw() {
        return 2;
    }

    public int numberOfCardsToKeep() {
        return 1;
    }

    @Override
    public String toString() {
        return super.toString() + " ($" + getCost() + ", " + getPoint() + " pts)";
    }
}
