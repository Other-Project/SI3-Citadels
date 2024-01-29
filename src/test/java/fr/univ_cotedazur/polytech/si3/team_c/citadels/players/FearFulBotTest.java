package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FearFulBotTest {
    FearFulBot bot1, bot2, bot3;

    @BeforeEach
    void setUp() {
        bot1 = new FearFulBot("fearFulBot1", 100, List.of(new Laboratory(), new Castle(), new Prison()));
        bot1.setPlayers(() -> new ArrayList<>(List.of(bot2, bot3)));
        bot2 = new FearFulBot("fearFulBot2", 1, List.of());
        bot3 = new FearFulBot("fearFulBot3", 100, List.of());
    }

    @Test
    void districtProfitabilityTest() {
        assertTrue(0 <= bot1.districtProfitability(bot1.getHandDistricts().get(0)));
        assertTrue(0 <= bot1.districtProfitability(bot1.getHandDistricts().get(1)));
        assertTrue(0 > bot1.districtProfitability(bot1.getHandDistricts().get(2)));
    }

    @Test
    void somebodyCouldDestroyDistrictTest() {
        bot1.buildDistrict(bot1.getHandDistricts().get(0), 0);
        assertTrue(bot1.couldDestroy(bot1.getBuiltDistricts().get(0), bot3));
        assertFalse(bot1.couldDestroy(bot1.getBuiltDistricts().get(0), bot2));
        assertEquals(List.of(bot3), bot1.possibleDestruction(List.of(bot3)));
        assertEquals(List.of(), bot1.possibleDestruction(List.of(bot2)));
        assertEquals(List.of(bot3), bot1.possibleDestruction(List.of(bot3, bot2)));
        assertEquals(12, bot1.destroyFear());
    }

    @Test
    void possibleExchangeTest() {
        assertEquals(2, bot1.possibleExchange(List.of(bot1, bot2, bot3)));
        assertEquals(11, bot1.exchangePlayerFear());
        bot3.addDistrictToHand(new Smithy());
        bot3.addDistrictToHand(new Battlefield());
        bot3.addDistrictToHand(new Church());
        assertEquals(1, bot1.possibleExchange(List.of(bot1, bot2, bot3)));
        assertEquals(9, bot1.exchangePlayerFear());
    }

    @Test
    void somebodyCouldKillTest() {
        bot1.buildDistrict(bot1.getHandDistricts().get(0), 0);
        assertEquals(2, bot1.possibleKill(bot1.getPlayers()));
        assertEquals(7, bot1.killFear());
        bot3.addDistrictToHand(new Smithy());
        bot3.addDistrictToHand(new Battlefield());
        bot3.addDistrictToHand(new Church());
        bot3.getHandDistricts().forEach(district -> bot3.buildDistrict(district, 0));
        assertEquals(1, bot1.possibleKill(bot1.getPlayers()));
        assertEquals(6, bot1.killFear());
    }

    @Test
    void somebodyCouldStealTest() {
        bot1.buildDistrict(bot1.getHandDistricts().get(0), 0);
        assertEquals(1, bot1.possibleSteal(bot1.getPlayers()));
        assertEquals(6, bot1.stealFear());
        bot3.pay(20);
        assertEquals(2, bot1.possibleSteal(bot1.getPlayers()));
        assertEquals(7, bot1.stealFear());
        bot3.gainCoins(100);
        bot2.gainCoins(100);
        assertEquals(0, bot1.possibleSteal(bot1.getPlayers()));
        assertEquals(0, bot1.stealFear());
    }
}
