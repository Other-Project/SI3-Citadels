package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
            bot1.buildDistrict(district, 0);
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
        List<District> firstBotDistricts = List.of(
                new Palace(), new Cathedral(), new TownHall(),
                new Fortress(), new TheKeep()
        );
        List<District> secondBotDistricts = List.of(
                new HauntedCity(), new Castle(), new Manor(),
                new Church(), new Monastery(), new Harbor(),
                new TradingPost(), new Market()
        );

        // First bot initialisation
        Bot firstBot = new Bot("bot1", 5000, firstBotDistricts);

        // Second bot initialisation
        Bot secondBot = new Bot("bot2", 5000, secondBotDistricts);

        // Districts building
        for (int i = 0; i < firstBotDistricts.size(); i++) firstBot.buildDistrict(firstBotDistricts.get(i), i);
        for (int i = 0; i < secondBotDistricts.size(); i++) secondBot.buildDistrict(secondBotDistricts.get(i), i);

        secondBot.endsGame();
        Game scriptedGame = new Game(List.of(firstBot, secondBot));
        assertEquals(new SimpleEntry<>(List.of(firstBot, secondBot), 26), scriptedGame.getWinners());
        assertEquals("There is an equality between players : bot1, bot2 with 26 points !", scriptedGame.winnersDisplay());

        // Test without equality

        // Third bot initialisation
        Bot thirdBot = new Bot("bot3", 1500, List.of(new Tavern(), new Library(), new Harbor()));
        for (int i = 0; i < secondBotDistricts.size(); i++) thirdBot.buildDistrict(secondBotDistricts.get(i), i);

        Game secondScriptedGame = new Game(List.of(firstBot, thirdBot));

        assertEquals(new SimpleEntry<>(List.of(firstBot), 26), secondScriptedGame.getWinners());
        assertEquals("The player bot1 won with 26 points !", secondScriptedGame.winnersDisplay());
    }

    @Test
    void testThiefTurn() {
        Bot trickedBot1 = new Bot("bot1", 0, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Thief()) ? new Thief() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }

            @Override
            public Character chooseCharacterToRob(List<Character> characterList) {
                return characterList.contains(new Merchant()) ? new Merchant() : characterList.get(0);
            }
        };
        Bot trickedBot2 = new Bot("bot2", 500, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Merchant()) ? new Merchant() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        // The bot1 will choose the thief and try to rob the merchant, which was chosen by bot2.
        game.addPlayer(trickedBot1);
        game.addPlayer(trickedBot2);
        game.gameTurn();
        assertTrue(trickedBot1.getCoins() >= 500);
    }

    @Test
    void testArchitectDrawing() {
        Bot trickedBot = new Bot("bot1", 0, Collections.emptyList()) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Architect()) ? new Architect() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }

            @Override
            public Action nextAction(Set<Action> remainingActions) {
                // If he doesn't get his income, the player will not build any district, so we can check that he has 4 districts in hand.
                remainingActions.remove(Action.INCOME);
                return super.nextAction(remainingActions);
            }
        };
        game.addPlayer(trickedBot);
        game.characterSelectionTurn();
        game.playerTurn(trickedBot);
        // trickedBot is the Architect, so he must draw 2 extra districts first
        assertTrue(trickedBot.getHandDistricts().size() >= 2);
    }
}