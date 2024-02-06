package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import com.opencsv.bean.CsvBindByName;

public class Statistic {
    @CsvBindByName(column = "Number of runs")
    @CsvPosition()
    private int runs;

    @CsvBindByName(column = "Name of the bot")
    @CsvPosition(position = 1)
    private String name;
    @CsvBindByName(column = "Number of wins")
    @CsvPosition(position = 2)
    private int wins;
    @CsvBindByName(column = "Percentage of wins")
    @CsvPosition(position = 3)
    private float winsPercentage;
    @CsvBindByName(column = "Number of equalities")
    @CsvPosition(position = 4)
    private int equalities;
    @CsvBindByName(column = "Percentage of equalities")
    @CsvPosition(position = 5)
    private float equalitiesPercentage;
    @CsvBindByName(column = "Number of losses")
    @CsvPosition(position = 6)
    private int losses;
    @CsvBindByName(column = "Percentage of losses")
    @CsvPosition(position = 7)
    private float lossesPercentage;

    /**
     * For OpenCSV
     */
    @SuppressWarnings("unused")
    public Statistic() {
    }

    public Statistic(String name) {
        this.name = name;
    }

    public void addWin() {
        wins++;
        runs++;
        winsPercentage = wins / (float) runs;
    }

    public void addLoss() {
        losses++;
        runs++;
        lossesPercentage = losses / (float) runs;
    }

    public void addEquality() {
        equalities++;
        runs++;
        equalitiesPercentage = equalities / (float) runs;
    }

    public int getWin() {
        return wins;
    }

    public int getLoose() {
        return losses;
    }

    public int getEquality() {
        return equalities;
    }

    public float getWinPercentage() {
        return winsPercentage;
    }

    public float getLoosePercentage() {
        return lossesPercentage;
    }

    public float getEqualityPercentage() {
        return equalitiesPercentage;
    }

    public int numberOfTest() {
        return runs;
    }
    @Override
    public String toString() {
        return "Win : " + getWinPercentage() + "% / Loose : " + getLoosePercentage() + "% / Equality : " + getEqualityPercentage() + "%";
    }
}
