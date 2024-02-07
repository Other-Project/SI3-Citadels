package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormatSymbols;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticTest {

    Statistic stat;

    @BeforeEach
    void setUp() {
        stat = new Statistic();
    }

    @Test
    void addWinTest() {
        stat.addWin(0);
        assertEquals(1, stat.getWin());
        stat.addWin(0);
        assertEquals(2, stat.getWin());
    }

    @Test
    void addLooseTest() {
        stat.addLoss(0);
        assertEquals(1, stat.getLosses());
        stat.addLoss(0);
        assertEquals(2, stat.getLosses());
    }

    @Test
    void addEqualityTest() {
        stat.addEquality(0);
        assertEquals(1, stat.getEquality());
        stat.addEquality(0);
        assertEquals(2, stat.getEquality());
    }

    @Test
    void getWinPercentageTest() {
        stat.addWin(0);
        assertEquals(100.0, stat.getWinPercentage());
        stat.addWin(0);
        assertEquals(100.0, stat.getWinPercentage());
    }

    @Test
    void getLoosePercentageTest() {
        stat.addLoss(0);
        assertEquals(100.0, stat.getLossPercentage());
        stat.addLoss(0);
        assertEquals(100.0, stat.getLossPercentage());
    }

    @Test
    void getEqualityPercentageTest() {
        stat.addEquality(0);
        assertEquals(100.0, stat.getEqualityPercentage());
        stat.addEquality(0);
        assertEquals(100.0, stat.getEqualityPercentage());
    }

    @Test
    void percentageTest() {
        stat.addWin(0);
        stat.addEquality(0);
        assertEquals(50.0, stat.getWinPercentage());
        assertEquals(50.0, stat.getEqualityPercentage());
        stat.addLoss(0);
        stat.addLoss(0);
        assertEquals(25.0, stat.getWinPercentage());
        assertEquals(25.0, stat.getEqualityPercentage());
        assertEquals(50.0, stat.getLossPercentage());
    }

    @Test
    void pointsTest() {
        stat.addWin(20);
        assertEquals(20, stat.getAveragePoints());
        stat.addEquality(19);
        assertEquals(19.5, stat.getAveragePoints());
        stat.addLoss(21);
        assertEquals(20, stat.getAveragePoints());
        stat.addWin(20);
        assertEquals(20, stat.getAveragePoints());
    }

    @Test
    void toStringTest() {
        stat.addWin(20);
        stat.addWin(22);
        stat.addLoss(25);
        var separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        assertEquals("Wins: 2 (66" + separator + "7%) ; Equalities: 0 (0" + separator + "0%) ; Losses: 1 (33" + separator + "3%) ; Avr points: 22" + separator + "3", stat.toString());
    }
}
