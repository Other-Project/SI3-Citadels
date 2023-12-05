package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public abstract class District extends Card {
    private final int cost;
    private final int point;

    protected District(String name, int cost, Colors color, int point) {
        super(name, color);
        this.cost = cost;
        this.point = point;
    }

    /**
     * @return the cost of the district
     **/
    public int getCost() {
        return cost;
    }

    /**
     * @return the points given by the district
     */
    public int getPoint() {
        return point;
    }

    /**
     * @return true if the district is destructible else false
     */
    public boolean isDestructible() {
        return true;
    }

    /**
     * @return the number of cards to draw after this district
     */
    public int numberOfCardsToDraw() {
        return 2;
    }

    /**
     * @return the number of cards to be drawn after this one
     */
    public int numberOfCardsToKeep() {
        return 1;
    }

    @Override
    public String toString() {
        return super.toString() + " ($" + getCost() + ", " + getPoint() + " pts)";
    }
}
