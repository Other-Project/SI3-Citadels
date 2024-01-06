package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

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
    void MagicianTest() {
        Bot bot2 = new Bot("Bot 2", 2, List.of(new Battlefield(), new Castle(), new Church(), new DragonGate(), new Docks(), new Laboratory()));

        Bot bot1 = new Bot("bot 1", 2, List.of(new Battlefield(), new Castle(), new Church(), new DragonGate())) {
            @Override
            public Set<Action> createActionSet() { //Override of the createActionSet in Player Method to manipulate the actionTest of the player and test the playerTurn method of Game
                setActionSet(new HashSet<>(getCharacter().orElseThrow().getAction().orElseThrow()));
                return getActionSet();
            }

        };
        bot1.pickCharacter(List.of(new Magician())); // Create a bot with the character magician
        bot2.pickCharacter(List.of(new King()));
        game.addPlayer(bot1);
        game.addPlayer(bot2);
        game.playerTurn(bot1); // The bot will exchange his cards with the other player
        assertEquals(List.of(new Battlefield(), new Castle(), new Church(), new DragonGate(), new Docks(), new Laboratory()), bot1.getHandDistricts());
        assertEquals(List.of(new Battlefield(), new Castle(), new Church(), new DragonGate()), bot2.getHandDistricts());
        game.playerTurn(bot1); // The bot will exchange some cards with the deck because the other player has fewer cards than him, and he has somme non-purple cards
        assertEquals(6, bot1.getHandDistricts().size());
    }

    @Test
    void gameObserverTest() {
        Game gameWithNumber = new Game(4);
        GameObserver gameObserver = gameWithNumber.getGameObserver();
        gameObserver.getCardsNumber().forEach((s, integer) -> assertEquals(2, (int) integer));
        gameObserver.getCoins().forEach((s, integer) -> assertEquals(2, (int) integer));
        gameWithNumber.getPlayerList().forEach(p -> assertEquals(2, (int) p.getGameStatus().getCardsNumber().get(p.getName())));
        gameWithNumber.getPlayerList().forEach(p -> assertEquals(2, (int) p.getGameStatus().getCoins().get(p.getName())));
        assertEquals(4, gameObserver.getPlayersNumber());
        Player p1 = new Bot("P1", 200, game.getDeck().draw(3));
        Player p2 = new Bot("P2", 10, game.getDeck().draw(7));
        Player p3 = new Bot("P3", 1, game.getDeck().draw(8));
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        gameObserver = game.getGameObserver();
        assertEquals(3, gameObserver.getPlayersNumber());
        assertEquals(200, gameObserver.getCoins().get("P1"));
        assertEquals(10, gameObserver.getCoins().get("P2"));
        assertEquals(1, gameObserver.getCoins().get("P3"));
        assertEquals(3, gameObserver.getCardsNumber().get("P1"));
        assertEquals(7, gameObserver.getCardsNumber().get("P2"));
        assertEquals(8, gameObserver.getCardsNumber().get("P3"));
        List<District> districtsBuilt = new ArrayList<>();
        districtsBuilt.add(p1.getHandDistricts().get(0));
        p1.buildDistrict(p1.getHandDistricts().get(0), 0);
        districtsBuilt.add(p1.getHandDistricts().get(0));
        p1.buildDistrict(p1.getHandDistricts().get(0), 0);
        assertEquals(1, gameObserver.getCardsNumber().get("P1"));
        assertEquals(districtsBuilt, gameObserver.getBuiltDistrict().get("P1"));
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

    @Test
    void testArchitectBuilding() {
        Bot trickedBot = new Bot("bot1", 500, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Architect()) ? new Architect() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        game.addPlayer(trickedBot);
        game.characterSelectionTurn();
        game.playerTurn(trickedBot);
        assertEquals(3, trickedBot.getBuiltDistricts().size());
    }

    @Test
    void warlordTest() {
        Bot warlordBot = new Bot("bot 1", 10, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot bot2 = new Bot("bot 2", 10, List.of(new Church(), new Monastery())) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Merchant()) ? new Merchant() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }

            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                if (remainingActions.contains(Action.SPECIAL_INCOME) && quantityOfColorBuilt(getCharacter().orElseThrow().getColor()) > 0)
                    return Action.SPECIAL_INCOME;// Pick coins according to the built districts if the ability of the chosen character allows it
                return Action.NONE;
            }

        };
        Game game2 = new Game();
        // game1 test
        game.addPlayer(warlordBot);
        game.addPlayer(bot2);
        game.characterSelectionTurn();
        game.playerTurn(bot2);
        assertEquals(1, bot2.getBuiltDistricts().size());
        game.playerTurn(bot2);
        assertEquals(2, bot2.getBuiltDistricts().size());
        game.playerTurn(warlordBot);
        assertEquals(1, bot2.getBuiltDistricts().size());
    }

    @Test
    void warlordAgainstBishopTest() {
        Bot warlordBot = new Bot("bot 1", 10, game.getDeck().draw(2)) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot bishopBot = new Bot("bot 3", 10, List.of(new Harbor())) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Bishop()) ? new Bishop() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }

            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                if (remainingActions.contains(Action.SPECIAL_INCOME) && quantityOfColorBuilt(getCharacter().orElseThrow().getColor()) > 0)
                    return Action.SPECIAL_INCOME;// Pick coins according to the built districts if the ability of the chosen character allows it
                return Action.NONE;
            }
        };
        game.addPlayer(warlordBot);
        game.addPlayer(bishopBot);
        game.characterSelectionTurn();
        game.playerTurn(bishopBot);
        assertEquals(1, bishopBot.getBuiltDistricts().size());
        game.playerTurn(warlordBot);
        assertEquals(1, bishopBot.getBuiltDistricts().size());
    }
}