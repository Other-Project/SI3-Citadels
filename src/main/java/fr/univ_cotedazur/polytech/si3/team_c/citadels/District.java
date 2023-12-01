package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public abstract class District{
    private final String name;
    private final int cost;
    private final Colors color;
    private final int point;

    protected District(String name, int cost, Colors color, int point){
        this.name = name;
        this.cost = cost;
        this.color = color;
        this.point = point;
    }

    public int getCost(){
        return cost;
    }

    public Colors getColor(){
        return color;
    }

    public String getName() {
        return name;
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
}
