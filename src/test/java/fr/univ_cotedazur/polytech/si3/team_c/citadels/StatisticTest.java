package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticTest {

    Statistic stat;

    @BeforeEach
    void setUp() {
        stat = new Statistic();
    }

    @Test
    void addWinTest() {
        stat.addWin();
        assertEquals(1, stat.getWin());
        stat.addWin();
        assertEquals(2, stat.getWin());
    }

    @Test
    void addLooseTest() {
        stat.addLoose();
        assertEquals(1, stat.getLoose());
        stat.addLoose();
        assertEquals(2, stat.getLoose());
    }

    @Test
    void addEqualityTest() {
        stat.addEquality();
        assertEquals(1, stat.getEquality());
        stat.addEquality();
        assertEquals(2, stat.getEquality());
    }

    @Test
    void getWinPercentageTest() {
        stat.addWin();
        assertEquals(100.0, stat.getWinPercentage());
        stat.addWin();
        assertEquals(100.0, stat.getWinPercentage());
    }

    @Test
    void getLoosePercentageTest() {
        stat.addLoose();
        assertEquals(100.0, stat.getLoosePercentage());
        stat.addLoose();
        assertEquals(100.0, stat.getLoosePercentage());
    }

    @Test
    void getEqualityPercentageTest() {
        stat.addEquality();
        assertEquals(100.0, stat.getEqualityPercentage());
        stat.addEquality();
        assertEquals(100.0, stat.getEqualityPercentage());
    }

    @Test
    void percentageTest() {
        stat.addWin();
        stat.addEquality();
        assertEquals(50.0, stat.getWinPercentage());
        assertEquals(50.0, stat.getEqualityPercentage());
        stat.addLoose();
        stat.addLoose();
        assertEquals(25.0, stat.getWinPercentage());
        assertEquals(25.0, stat.getEqualityPercentage());
        assertEquals(50.0, stat.getLoosePercentage());
    }

    @Test
    void toStringTest() {
        stat.addWin();
        assertEquals("Win : 100.0% / Loose : 0.0% / Equality : 0.0%", stat.toString());
    }
}
