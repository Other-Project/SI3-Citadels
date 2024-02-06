package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public class Statistic {

    private double win;
    private double loose;
    private double equality;

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
        win = 0.;
        loose = 0.;
        equality = 0.;
    }

    public void getPourcent(double numberTest) {
        win = win * 100. / numberTest;
        loose = loose * 100. / numberTest;
        equality = equality * 100. / numberTest;
    }

    @Override
    public String toString() {
        return "Win : " + win + "% / Loose : " + loose + "% / Equality : " + equality + "%";
    }
}
