package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.Optional;

@SuppressWarnings("java:S2160")
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
    public Optional<Integer> numberOfDistrictsToDraw() {
        return Optional.empty();
    }

    /**
     * @return the number of cards to be drawn after this one
     */
    public Optional<Integer> numberOfDistrictsToKeep() {
        return Optional.empty();
    }

    /**
     * @param color the color to be compared
     * @return true if the card color match to the color
     */
    public boolean matchColor(Colors color) {
        return getColor().equals(color);
    }

    @Override
    public String toString() {
        return super.toString() + " ($" + getCost() + ", " + getPoint() + " pts)";
    }
}
