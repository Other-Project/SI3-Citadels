package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    private Deck deck;
    @BeforeEach
    void setup() {
        game = new Game();
        deck = new Deck();
    }

    @Test
        // Here, we are testing if the game is ending (that the method start executes well until the end)
    void start() {
        game.start();
        assertTrue(game.getPlayerList().contains(game.getWinner()));
    }

    @Test
    void defaultCharacterList() {
        List<Character> characterList = new ArrayList<>(List.of(new Assassin(), new Thief(), new Magician(), new King(),
                new Bishop(), new Merchant(), new Architect(), new Warlord()));
        assertEquals(characterList, game.defaultCharacterList());
    }

    @Test
    void end() {
        Bot bot1 = new Bot("bot1", 1500, List.of(new Temple(), new Battlefield(), new Castle()
                , new Cathedral(), new Church(), new Docks(), new DragonGate(), new Fortress()));
        game.addPlayer(bot1);
        assertFalse(game.end(bot1));
        for (District district : bot1.getHandDistricts()) {
            bot1.buildDistrict(district);
        }
        assertTrue(game.end(bot1));
    }

    @Test
    void gameTurn() {
        Bot bot1 = new Bot("bot1", 2, deck.draw(2));
        game.addPlayer(bot1);
        assertFalse(game.gameTurn());
    }
}