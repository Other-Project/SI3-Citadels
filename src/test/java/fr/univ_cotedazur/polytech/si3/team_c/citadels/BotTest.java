package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Architect;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Magician;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    Player player1;
    Bot bot1;

    @BeforeEach
    void setUp() {
        player1 = bot1 = new Bot("Bot 1", 2, List.of(new Battlefield(), new Castle(), new Church(), new DragonGate()));
    }

    @Test
    void stringRepresentation() {
        assertEquals("Bot 1", player1.getName());
        player1.buildDistrict(new Church(), 0);
        assertEquals("Bot 1 (0 coins) [🔴Battlefield ($3, 3 pts), 🟡Castle ($4, 4 pts), 🟣Dragon Gate ($6, 8 pts)] :\n\t🔵Church ($2, 2 pts)", player1.toString());
    }

    @Test
    void coins() {
        assertEquals(2, player1.getCoins());
        assertFalse(player1.pay(3));
        assertTrue(player1.pay(2));
        assertFalse(player1.pay(-1));
        assertEquals(0, player1.getCoins());
        assertEquals(Action.INCOME, player1.nextAction(List.of(Action.DRAW, Action.INCOME))); // The player has no money and should therefore get some gold
    }

    @Test
    void objective() {
        var objective = bot1.districtObjective();
        assertTrue(objective.isPresent());
        assertEquals(objective, bot1.districtObjective()); // The objective should be consistant
        assertEquals(new DragonGate(), objective.get());

        Bot bot = new Bot(player1.getName(), player1.getCoins(), List.of(new Church(), new Market(), new Prison(), new TradingPost()));
        objective = bot.districtObjective();
        assertTrue(objective.isPresent());
        assertEquals(objective, bot.districtObjective()); // Even with all profitability being equals the objective should be consistant
        assertEquals(new Church(), objective.get());
    }

    @Test
    void actions() {
        assertEquals(Action.INCOME, player1.nextAction(List.of(Action.DRAW, Action.INCOME, Action.BUILD))); // The player already has a lot of card, he should get some coins
        player1.gainCoins(4); // To be sure the player will build something we give 4 golds and not wait for having enough to build the DragonGate
        assertEquals(Action.BUILD, player1.nextAction(List.of(Action.BUILD)));
        assertTrue(player1.buildDistrict(new DragonGate(), 0));
        assertEquals(List.of(new DragonGate()), player1.getBuiltDistricts());
        assertEquals(0, player1.getCoins());

        assertEquals(Action.INCOME, player1.nextAction(List.of(Action.DRAW, Action.INCOME, Action.BUILD))); // The player has no gold he should definitely get some
        player1.gainCoins(1); // So the player can't build anything
        assertEquals(Action.NONE, player1.nextAction(List.of(Action.BUILD))); // The player has nothing to build, he should end his turn there

        player1.gainCoins(20); // So the player have enough to build everything in hand, and so he doesn't get coins
        for (var district : player1.getHandDistricts())
            assertTrue(player1.buildDistrict(district, 0)); // We build everything remaining to force the player to draw cards
        assertEquals(Action.DRAW, player1.nextAction(List.of(Action.DRAW, Action.INCOME, Action.BUILD))); // The player already has a lot of gold but no cards
    }

    @Test
    void pickCharacter() {
        assertTrue(player1.getCharacter().isEmpty());
        assertEquals(new Architect(), player1.pickCharacter(List.of(new Architect())));
        assertEquals(new Architect(), player1.getCharacter().get());
        assertEquals(new Magician(), player1.pickCharacter(List.of(new Magician())));
        assertEquals(new Magician(), player1.getCharacter().get());
    }

    @Test
    void pickDistrictsFromDeck() {
        // The player already has a castle in hand, since duplicates are not allowed, he should take the other one
        assertEquals(List.of(new Cathedral()), player1.pickDistrictsFromDeck(List.of(new Castle(), new Cathedral()), 1));

        // The player already has a castle in hand, he should take the other two
        assertEquals(List.of(new Fortress(), new Cathedral()), player1.pickDistrictsFromDeck(List.of(new Fortress(), new Cathedral(), new Church()), 2));

        // The player has no choice but to take a church, even if he already has one
        assertEquals(List.of(new Church()), player1.pickDistrictsFromDeck(List.of(new Church(), new Church()), 1));

        assertEquals(1, player1.pickDistrictsFromDeck(List.of(new Fortress(), new Cathedral()), 1).size());
        assertEquals(2, player1.pickDistrictsFromDeck(List.of(new Fortress(), new Cathedral()), 2).size());
        assertEquals(1, player1.pickDistrictsFromDeck(List.of(new Fortress(), new Cathedral(), new Graveyard()), 1).size());
    }

    @Test
    void pickDistrictsToBuild() {
        Bot bot = new Bot("bot", 9, List.of(new Battlefield(), new Castle(), new DragonGate(), new Church())) {
            @Override
            protected double districtProfitability(District district) {
                return getHandDistricts().size() - getHandDistricts().indexOf(district); // This bot wants to build the card in the order there are in his hand
            }
        };
        assertEquals(List.of(new Battlefield()), bot.pickDistrictsToBuild(0)); // Only one district should be built, and it should be the first in his hand
        assertEquals(List.of(new Battlefield()), bot.getBuiltDistricts()); // The district has been correctly built
        assertEquals(List.of(new Castle()), bot.pickDistrictsToBuild(2)); // The player can build 2 districts but only one of his objective can be afforded
        assertEquals(List.of(new Battlefield(), new Castle()), bot.getBuiltDistricts());
    }

    @Test
    void getScore() {
        // Test bonus score with first player to end bonus and districts score
        player1.gainCoins(10000);
        List<District> districts = List.of(
                new Battlefield(), new Castle(), new Monastery(),
                new TheKeep(), new Palace(), new Temple(),
                new Manor(), new Prison(), new HauntedCity());
        for (int i = 0; i < districts.size(); i++) {
            player1.addDistrictToHand(districts.get(i));
            player1.buildDistrict(districts.get(i), i);
        }
        player1.endsGame();
        assertEquals(26, player1.getDistrictsScore());
        assertEquals(30, player1.getScore(8));

        // Test bonus score with districts score, colors bonus and eight districts bonus
        setUp();
        player1.gainCoins(10000);
        for (District district : districts) player1.addDistrictToHand(district);
        for (int i = districts.size() - 1; i >= 0; i--) {
            player1.buildDistrict(districts.get(i), districts.size() - i);
        }
        assertEquals(26, player1.getDistrictsScore());
        assertEquals(31, player1.getScore(8));
    }

    @Test
    void numberOfDistrictsToDraw() {
        Observatory observatory = new Observatory();
        Bot bot1 = new Bot("bot1", 1500, List.of(new Temple(), new Battlefield(), new Castle()
                , new Cathedral(), observatory, new Docks(), new DragonGate(), new Fortress()));
        assertEquals(2, bot1.numberOfDistrictsToDraw());
        for (District district : bot1.getHandDistricts()) {
            if (!district.equals(observatory)) bot1.buildDistrict(district, 0);
        }
        assertEquals(2, bot1.numberOfDistrictsToDraw());
        bot1.buildDistrict(observatory, 0);
        assertEquals(3, bot1.numberOfDistrictsToDraw());
    }

    @Test
    void numberOfDistrictsToKeep() {
        Library library = new Library();
        Bot bot1 = new Bot("bot1", 1500, List.of(new Temple(), new Battlefield(), new Castle()
                , new Cathedral(), library, new Docks(), new DragonGate(), new Fortress()));
        assertEquals(1, bot1.numberOfDistrictsToKeep());
        for (District district : bot1.getHandDistricts()) {
            if (!district.equals(library)) bot1.buildDistrict(district, 0);
        }
        assertEquals(1, bot1.numberOfDistrictsToKeep());
        bot1.buildDistrict(library, 0);
        assertEquals(2, bot1.numberOfDistrictsToKeep());
    }
}