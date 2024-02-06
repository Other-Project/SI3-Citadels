package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public class Statistic {

    private int win;
    private int loose;
    private int equality;


    public void addWin() {
        win++;
    }

    public void addLoose() {
        loose++;
    }

    public void addEquality() {
        equality++;
    }

    public double getWin() {
        return win;
    }

    public double getLoose() {
        return loose;
    }

    public double getEquality() {
        return equality;
    }

    public Statistic() {
        win = 0;
        loose = 0;
        equality = 0;
    }

    public float getWinPercentage() {
        return win * 100f / numberOfTest();
    }

    public float getLoosePercentage() {
        return loose * 100f / numberOfTest();
    }

    public float getEqualityPercentage() {
        return equality * 100f / numberOfTest();
    }

    public int numberOfTest() {
        return win + loose + equality;
    }
    @Override
    public String toString() {
        return "Win : " + getWinPercentage() + "% / Loose : " + getLoosePercentage() + "% / Equality : " + getEqualityPercentage() + "%";
    }
}
