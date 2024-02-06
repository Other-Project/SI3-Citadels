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
    void getPourcentTest() {
        stat.addWin();
        stat.addLoose();
        stat.addEquality();
        stat.getPourcent(100);
        assertEquals(1, stat.getWin());
        assertEquals(1, stat.getLoose());
        assertEquals(1, stat.getEquality());
    }

    @Test
    void toStringTest() {
        stat.addWin();
        stat.addLoose();
        stat.addEquality();
        assertEquals("Win : 1.0% / Loose : 1.0% / Equality : 1.0%", stat.toString());
    }
}
