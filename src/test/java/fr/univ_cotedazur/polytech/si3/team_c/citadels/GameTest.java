package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game, game1, game2;
    @BeforeEach
    void setup() {
        game = new Game();
        game1 = new Game(1);
        game2 = new Game(2);
    }

    @Test
        // Here, we are testing if the game is ending (that the method start executes well until the end)
    void start() {
        game1.start();
        assertTrue(game1.getPlayerList().contains(game1.getWinner()));
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
        Bot bot1 = new Bot("bot1", 2, game.getDeck().draw(2));
        game.addPlayer(bot1);
        game.setDefaultDeck();
        assertFalse(game.gameTurn());
    }

    @Test
    void crownTest() {
        Bot bot1 = new BotOnlyKing("bot1", 2, game.getDeck().draw(2));
        Bot bot2 = new Bot("bot2", 2, game.getDeck().draw(2));
        game.addPlayer(bot1);
        game.addPlayer(bot2);
        bot1.pickCharacter(List.of());
        bot2.pickCharacter(List.of(new Magician()));
        game.playerTurn(bot1);
        assertEquals("bot1", game.getPlayerList().get(game.getCrown()).getName());
        assertEquals(0, game.getCrown());
        game.playerTurn(bot2);
        assertEquals("bot1", game.getPlayerList().get(game.getCrown()).getName());
        assertEquals(0, game.getCrown());
        game.gameTurn();
        assertEquals("bot1", game.getPlayerOrder().get(game.getCrown()).getName());
        assertEquals(0, game.getCrown());

    }


}