package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import com.opencsv.bean.CsvBindByName;

import java.text.DecimalFormat;

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

    @CsvBindByName(column = "Average points")
    @CsvPosition(position = 8)
    private float averagePoints;

    /**
     * For OpenCSV
     */
    @SuppressWarnings("unused")
    public Statistic() {
    }

    public Statistic(String name) {
        this.name = name;
    }

    public void addWin(int points) {
        wins++;
        runned(points);
    }

    public void addLoss(int points) {
        losses++;
        runned(points);
    }

    public void addEquality(int points) {
        equalities++;
        runned(points);
    }

    private void runned(int points) {
        averagePoints = (averagePoints * runs + points) / ++runs;
        winsPercentage = wins * 100f / runs;
        equalitiesPercentage = equalities * 100f / runs;
        lossesPercentage = losses * 100f / runs;
    }

    public String getName() {
        return name;
    }

    public int getWin() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getEquality() {
        return equalities;
    }

    public float getWinPercentage() {
        return winsPercentage;
    }

    public float getLossPercentage() {
        return lossesPercentage;
    }

    public float getEqualityPercentage() {
        return equalitiesPercentage;
    }

    public float getAveragePoints() {
        return averagePoints;
    }

    public int numberOfTest() {
        return runs;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.0");
        return "Wins: " + getWin() + " (" + df.format(getWinPercentage())
                + "%) ; Equalities: " + getEquality() + " (" + df.format(getEqualityPercentage())
                + "%) ; Losses: " + getLosses() + " (" + df.format(getLossPercentage())
                + "%) ; Avr points: " + df.format(getAveragePoints());
    }
}
