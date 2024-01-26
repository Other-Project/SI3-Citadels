package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.Castle;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.Laboratory;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.Prison;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FearFulBotTest {
    FearFulBot bot1, bot2, bot3;

    @BeforeEach
    void setUp() {
        bot1 = new FearFulBot("fearFulBot1", 100, List.of(new Laboratory(), new Castle(), new Prison()));
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
        assertTrue(bot1.possibleDestruction(List.of(bot3)));
        assertFalse(bot1.possibleDestruction(List.of(bot2)));
        assertTrue(bot1.possibleDestruction(List.of(bot3, bot2)));
    }
}
