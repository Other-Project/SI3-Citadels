package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    @BeforeEach
    void setup() {
        game = new Game();
    }

    @Test
        // Here, we are testing if the game is ending (that the method start executes well until the end)
    void start() {
        game.addPlayer(new Bot("bot1", 2, game.getDeck().draw(2)));
        game.start();
        assertEquals(game.getPlayerList(), game.getWinners().getKey());
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
        bot1.pickCharacter(List.of(new King()));
        bot2.pickCharacter(List.of(new Magician()));
        for (Player p : game.getPlayerList()) {  //To test the crown feature in the playerTurn
            game.playerTurn(p);
            assertEquals(bot1, game.getPlayerList().get(game.getCrown()));
        }
        game.gameTurn();//To test the crown feature in the gameTurn and test if the crown does not turn between player if there is a King
        assertEquals(bot1, game.getPlayerList().get(game.getCrown()));
    }

    @Test
    void crownTestWithoutKingPlayer() {
        Bot bot1 = new Bot("bot1", 2, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                for (Character character : availableCharacters)
                    if (!(character instanceof King)) {
                        setCharacter(character);
                        return character;
                    }
                throw new UnsupportedOperationException();
            }
        };
        Bot bot2 = new Bot("bot2", 2, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                for (Character character : availableCharacters)
                    if (!(character instanceof King)) {
                        setCharacter(character);
                        return character;
                    }
                throw new UnsupportedOperationException();
            }
        };
        game.addPlayer(bot1);
        game.addPlayer(bot2);
        List<Character> availableCharacters = new ArrayList<>(List.of(new Assassin(), new Thief(), new Magician(), new King(),
                new Bishop(), new Merchant(), new Architect(), new Warlord()));

        availableCharacters.remove(bot1.pickCharacter(availableCharacters));
        bot2.pickCharacter(availableCharacters);
        game.setCrown(0);
        for (Player p : game.getPlayerList()) { //To test the crown feature in the playerTurn
            game.playerTurn(p);
            assertEquals(bot1, game.getPlayerList().get(game.getCrown()));
        }
        game.gameTurn();//To test the crown feature in the gameTurn and test if the crown turns between player if there is no King
        assertEquals(bot2, game.getPlayerList().get(game.getCrown()));
    }

    @Test
    void winnersTest() {
        // Equality test
        Bot firstBot = new Bot("bot1", 1500, List.of(new Tavern(), new Laboratory(), new Harbor()));
        Bot secondBot = new Bot("bot2", 1500, List.of(new Temple(), new Library(), new Docks()));
        for (District district : firstBot.getHandDistricts()) firstBot.buildDistrict(district);
        for (District district : secondBot.getHandDistricts()) secondBot.buildDistrict(district);
        Game scriptedGame = new Game(List.of(firstBot, secondBot));
        assertEquals(new SimpleEntry<>(List.of(firstBot, secondBot), firstBot.getScore()), scriptedGame.getWinners());
        assertEquals("There is an equality between players : bot1, bot2 with " + firstBot.getScore() + " points !", scriptedGame.winnersDisplay());

        // Normal test
        Bot thirdBot = new Bot("bot3", 1500, List.of(new Tavern(), new Library(), new Harbor()));
        Game secondScriptedGame = new Game(List.of(firstBot, thirdBot));
        for (District district : thirdBot.getHandDistricts()) thirdBot.buildDistrict(district);
        assertEquals(new SimpleEntry<>(List.of(thirdBot), thirdBot.getScore()), secondScriptedGame.getWinners());
        assertEquals("The player bot3 won with " + thirdBot.getScore() + " points !", secondScriptedGame.winnersDisplay());
    }
}