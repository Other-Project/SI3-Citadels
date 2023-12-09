package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game, game1;
    private final Random random = new Random();
    @BeforeEach
    void setup() {
        game = new Game();
        game1 = new Game(1);
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
    void crownTestWithKingPlayer() {
        Bot bot1 = new Bot("bot1", 2, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                setCharacter(new King());
                return getCharacter().orElseThrow();
            }
        };
        Bot bot2 = new Bot("bot2", 2, game.getDeck().draw(2));
        game.addPlayer(bot1);
        game.addPlayer(bot2);
        bot1.pickCharacter(List.of());
        bot2.pickCharacter(List.of(new Magician()));
        for (Player p : game.getPlayerList()) {  //To test the crown feature in the playerTurn
            game.playerTurn(p);
            assertEquals("bot1", game.getPlayerList().get(game.getCrown()).getName());
            assertEquals(0, game.getCrown());
        }
        game.gameTurn();//To test the crown feature in the gameTurn and test if the crown does not turn between player if there is a King
        assertEquals("bot1", game.getPlayerList().get(game.getCrown()).getName());
        assertEquals(0, game.getCrown());
    }

    @Test
    void crownTestWithoutKingPlayer() {
        Bot bot1 = new Bot("bot1", 2, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                List<Character> availableCharacters2 = new ArrayList<>(List.of(new Assassin(), new Thief(), new Magician(),
                        new Bishop(), new Merchant(), new Architect(), new Warlord()));
                setCharacter(availableCharacters2.get(random.nextInt(availableCharacters2.size())));
                return getCharacter().orElseThrow();
            }
        };
        Bot bot2 = new Bot("bot2", 2, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                List<Character> availableCharacters2 = new ArrayList<>(List.of(new Assassin(), new Thief(), new Magician(),
                        new Bishop(), new Merchant(), new Architect(), new Warlord()));
                setCharacter(availableCharacters2.get(random.nextInt(availableCharacters2.size())));
                return getCharacter().orElseThrow();
            }
        };
        game.addPlayer(bot1);
        game.addPlayer(bot2);
        bot1.pickCharacter(List.of());
        bot2.pickCharacter(List.of());
        game.setCrown(0);
        for (Player p : game.getPlayerList()) { //To test the crown feature in the playerTurn
            game.playerTurn(p);
            assertEquals("bot1", game.getPlayerList().get(game.getCrown()).getName());
            assertEquals(0, game.getCrown());
        }
        game.gameTurn();//To test the crown feature in the gameTurn and test if the crown turns between player if there is no King
        assertEquals("bot2", game.getPlayerList().get(game.getCrown()).getName());
        assertEquals(2, game.getCrown());
    }


}