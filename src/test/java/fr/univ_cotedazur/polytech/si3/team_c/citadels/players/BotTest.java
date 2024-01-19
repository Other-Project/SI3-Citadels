package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Game;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.*;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.districts.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    Player player1, player2;
    Bot bot1, bot2;

    @BeforeEach
    void setUp() {
        player1 = bot1 = new Bot("Bot 1", 2, List.of(new Battlefield(), new Castle(), new Church(), new DragonGate()));
        player2 = bot2 = new Bot("Bot 2", 2, List.of(new Battlefield(), new Castle(), new Church(), new DragonGate(), new Docks(), new Laboratory()));
    }

    @Test
    void coins() {
        assertEquals(2, player1.getCoins());
        assertFalse(player1.pay(3));
        assertTrue(player1.pay(2));
        assertFalse(player1.pay(-1));
        assertEquals(0, player1.getCoins());
        assertEquals(Action.INCOME, player1.nextAction(Set.of(Action.DRAW, Action.INCOME))); // The player has no money and should therefore get some gold
    }

    @Test
    void objective() {
        var objective = bot1.districtObjective();
        assertTrue(objective.isPresent());
        assertEquals(objective, bot1.districtObjective()); // The objective should be consistent
        assertEquals(new DragonGate(), objective.get());

        Bot bot = new Bot(player1.getName(), 1500, List.of(new Cathedral(), new Palace(), new TownHall(), new Fortress()));
        objective = bot.districtObjective();
        assertTrue(objective.isPresent());
        assertEquals(objective, bot.districtObjective()); // Even with all profitability being equals the objective should be consistent
    }

    @Test
    void basicActions() {
        assertEquals(Action.INCOME, player1.nextAction(Set.of(Action.DRAW, Action.INCOME, Action.BUILD))); // The player already has a lot of card, he should get some coins
        player1.gainCoins(4); // To be sure the player will build something we give 4 golds and not wait for having enough to build the DragonGate
        assertEquals(Action.BUILD, player1.nextAction(Set.of(Action.BUILD)));
        assertTrue(player1.buildDistrict(new DragonGate(), 0));
        assertEquals(List.of(new DragonGate()), player1.getBuiltDistricts());
        assertEquals(0, player1.getCoins());

        assertEquals(Action.INCOME, player1.nextAction(Set.of(Action.DRAW, Action.INCOME, Action.BUILD))); // The player has no gold he should definitely get some
        player1.gainCoins(1); // So the player can't build anything
        assertEquals(Action.NONE, player1.nextAction(Set.of(Action.BUILD))); // The player has nothing to build, he should end his turn there

        player1.gainCoins(20); // So the player have enough to build everything in hand, and so he doesn't get coins
        for (var district : player1.getHandDistricts())
            assertTrue(player1.buildDistrict(district, 0)); // We build everything remaining to force the player to draw cards
        assertEquals(Action.DRAW, player1.nextAction(Set.of(Action.DRAW, Action.INCOME, Action.BUILD))); // The player already has a lot of gold but no cards
    }

    @Test
    void specialIncome() {
        player1.addDistrictToHand(new WatchTower());
        player1.addDistrictToHand(new Prison());
        player1.setCharacter(new King());
        player1.gainCoins(6);
        assertTrue(player1.buildDistrict(new DragonGate(), 0));
        assertEquals(0, player1.gainSpecialIncome()); // The player has no yellow districts

        assertEquals(Action.INCOME, player1.nextAction(Set.of(Action.DRAW, Action.BUILD, Action.SPECIAL_INCOME, Action.INCOME))); // The player has a lot of cards in hand
        assertEquals(2, player1.gainIncome());
        assertEquals(4, player1.getCoins());
        var action = player1.nextAction(Set.of(Action.BUILD, Action.SPECIAL_INCOME));
        assertNotEquals(Action.SPECIAL_INCOME, action); // The player didn't build anything, he clearly shouldn't claim his special income now
        assertEquals(action, player1.nextAction(Set.of(Action.BUILD, Action.SPECIAL_INCOME))); // The player shouldn't change his mind
        assertEquals(Action.NONE, player1.nextAction(Set.of(Action.SPECIAL_INCOME))); // The player has no yellow districts built

        assertEquals(Action.INCOME, player1.nextAction(Set.of(Action.DRAW, Action.BUILD, Action.SPECIAL_INCOME, Action.INCOME))); // The player has a lot of cards in hand
        assertEquals(2, player1.gainIncome());
        assertEquals(6, player1.getCoins());
        assertEquals(Action.BUILD, player1.nextAction(Set.of(Action.BUILD, Action.SPECIAL_INCOME))); // The player has enough to build whatever he wants
        assertTrue(player1.buildDistrict(new WatchTower(), 0));
        assertEquals(Action.NONE, player1.nextAction(Set.of(Action.SPECIAL_INCOME))); // The player has no yellow districts built

        player1.pickCharacter(List.of(new Merchant(), new King(), new Bishop()));
        assertEquals(Action.INCOME, player1.nextAction(Set.of(Action.DRAW, Action.BUILD, Action.SPECIAL_INCOME, Action.INCOME))); // The player has a lot of cards in hand
        assertEquals(2, player1.gainIncome());
        assertEquals(7, player1.getCoins());
        assertEquals(Action.BUILD, player1.nextAction(Set.of(Action.BUILD, Action.SPECIAL_INCOME))); // The player has enough to build whatever he wants
        assertTrue(player1.buildDistrict(new Prison(), 0)); // We force the player to build a red card (so we can test his special income)
        assertEquals(Action.NONE, player1.nextAction(Set.of(Action.SPECIAL_INCOME))); // The player has no card of the color of his character
        assertEquals(0, player1.gainSpecialIncome());

        assertEquals(new Warlord(), player1.pickCharacter(List.of(new Merchant(), new Warlord()))); // The warlord is more profitable as the player will gain at least one more coins that with the merchant
        assertEquals(2, player1.gainSpecialIncome()); // The player has 2 red cards
    }

    @Test
    void pickCharacterSetsPlayer() {
        assertTrue(player1.getCharacter().isEmpty());
        assertEquals(new Architect(), player1.pickCharacter(List.of(new Architect())));
        assertEquals(new Architect(), player1.getCharacter().get());
        assertEquals(new Magician(), player1.pickCharacter(List.of(new Magician())));
        assertEquals(new Magician(), player1.getCharacter().get());
    }

    @Test
    void pickCharacterDecision() {
        List<Character> AllCharacters = List.of(new Assassin(), new Thief(), new Magician(), new King(),
                new Bishop(), new Merchant(), new Architect(), new Warlord());
        Player feared = new Bot("feared", 100, List.of(new Graveyard(), new Church(), new Castle(), new Smithy(), new Cathedral(), new DragonGate(), new Monastery()));

        List<Character> characters = new ArrayList<>(AllCharacters);
        assertEquals(new Architect(), player1.pickCharacter(characters)); // The bot should choose the architect as he gains 2 more coins
        characters.remove(new Architect());
        assertEquals(new Merchant(), player1.pickCharacter(characters)); // The bot should choose the merchant as he gains 1 more coin
        List<District> someBlueDistricts = List.of(new Monastery(), new Cathedral());
        for (District district : someBlueDistricts) {
            player1.addDistrictToHand(district);
            player1.gainCoins(district.getCost());
            assertTrue(player1.buildDistrict(district, 0));
        }
        assertEquals(new Bishop(), player1.pickCharacter(characters)); // The bot should choose the merchant as he gains 2 more coins and some security (from the warlord)
        for (District district : someBlueDistricts) player1.removeDistrictFromDistrictBuilt(district);


        player1.setPlayers(() -> List.of(feared));
        characters = new ArrayList<>(AllCharacters);
        assertEquals(new Thief(), player1.pickCharacter(characters)); // The bot should choose the thief as the other player has a lot of money

        feared.pay(100);
        player1.gainCoins(10);
        player1.removeFromHand(List.of(new DragonGate()));
        assertEquals(0, feared.getCoins());
        assertEquals(new Magician(), player1.pickCharacter(characters)); // The bot should choose the magician as the other player has a lot of cards, the bot already have some money and none of his card are really valuable

        for (District district : feared.getHandDistricts()) {
            feared.gainCoins(district.getCost());
            assertTrue(feared.buildDistrict(district, 0));
        }
        Character picked = player1.pickCharacter(characters);
        assertTrue(List.of(new Assassin(), new Warlord()).contains(picked), picked::toString); // The bot should try to prevent the other player from winning
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
        Bot bot2 = new Bot("bot", 15, List.of(new Temple(), new Church(), new Temple(), new Harbor())) {
            @Override
            protected double districtProfitability(District district) {
                return getHandDistricts().size() - getHandDistricts().indexOf(district); // This bot wants to build the card in the order there are in his hand
            }
        };
        bot.setCharacter(new Bishop());
        assertEquals(List.of(new Battlefield()), bot.pickDistrictsToBuild(0)); // Only one district should be built, and it should be the first in his hand
        assertEquals(List.of(new Battlefield()), bot.getBuiltDistricts()); // The district has been correctly built
        assertEquals(List.of(new Castle()), bot.pickDistrictsToBuild(2)); // The player can build 2 districts but only one of his objective can be afforded
        assertEquals(List.of(new Battlefield(), new Castle()), bot.getBuiltDistricts());
        bot2.setCharacter(new Thief());
        assertTrue(bot2.buildDistrict(new Temple(), 0));
        assertEquals(List.of(new Temple()), bot2.getBuiltDistricts());
        assertTrue(bot2.buildDistrict(new Church(), 0));
        assertEquals(List.of(new Temple(), new Church()), bot2.getBuiltDistricts());
        assertTrue(bot2.buildDistrict(new Harbor(), 0));
        assertFalse(bot2.buildDistrict(new Temple(), 0));
        assertEquals(List.of(new Temple(), new Church(), new Harbor()), bot2.getBuiltDistricts());

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

    @Test
    void createActionSetTest() {
        player1.pickCharacter(List.of(new King()));
        player1.createActionSet();
        assertEquals(new HashSet<>(List.of(Action.BUILD, Action.SPECIAL_INCOME, Action.DRAW, Action.INCOME)), player1.getActionSet());
        player1.pickCharacter(List.of(new Bishop()));
        player1.createActionSet();
        assertEquals(new HashSet<>(List.of(Action.BUILD, Action.SPECIAL_INCOME, Action.DRAW, Action.INCOME)), player1.getActionSet());
        player1.pickCharacter(List.of(new Warlord()));
        player1.createActionSet();
        assertEquals(new HashSet<>(List.of(Action.BUILD, Action.SPECIAL_INCOME, Action.DRAW, Action.INCOME, Action.DESTROY)), player1.getActionSet());
        player1.pickCharacter(List.of(new Merchant()));
        player1.createActionSet();
        assertEquals(new HashSet<>(List.of(Action.BUILD, Action.SPECIAL_INCOME, Action.DRAW, Action.INCOME)), player1.getActionSet());
        bot1.gainCoins(10);
        bot1.addDistrictToHand(new Smithy());
        bot1.addDistrictToHand(new Laboratory());
        bot1.buildDistrict(new Smithy(), 0);
        bot1.buildDistrict(new Laboratory(), 0);
        bot1.createActionSet();
        assertEquals(new HashSet<>(List.of(Action.BUILD, Action.SPECIAL_INCOME, Action.DRAW, Action.INCOME, Action.TAKE_THREE, Action.DISCARD)), player1.getActionSet());
    }

    @Test
    void removeActionSetTest() {
        player1.pickCharacter(List.of(new King()));
        player1.createActionSet();
        assertTrue(player1.removeAction(Action.SPECIAL_INCOME));
        assertEquals(new HashSet<>(List.of(Action.BUILD, Action.DRAW, Action.INCOME)), player1.getActionSet());
        assertTrue(player1.removeAction(Action.INCOME));
        assertEquals(new HashSet<>(List.of(Action.BUILD)), player1.getActionSet());
        assertTrue(player1.removeAction(Action.BUILD));
        assertEquals(new HashSet<>(), player1.getActionSet());
        assertFalse(player1.removeAction(Action.BUILD));
        assertEquals(new HashSet<>(), player1.getActionSet());
    }


    @Test
    void MagicianTest() {
        Bot bot1 = new Bot("bot 1", 2, List.of(new Battlefield(), new Castle(), new Church(), new DragonGate())) {
            @Override
            public Set<Action> createActionSet() { //Override of the createActionSet in Player Method to manipulate the actionTest of the player and test the playerTurn method of Game
                setActionSet(new HashSet<>(getCharacter().orElseThrow().getAction()));
                return getActionSet();
            }

        };
        bot1.setPlayers(() -> List.of(bot2));
        bot2.setPlayers(() -> List.of(bot1));
        bot1.pickCharacter(List.of(new Magician())); // Create a bot with the character magician
        bot2.pickCharacter(List.of(new King()));
        assertEquals(Set.of(Action.EXCHANGE_DECK, Action.EXCHANGE_PLAYER), bot1.createActionSet());
        assertEquals(List.of(new Battlefield(), new Castle(), new Church()), bot1.chooseCardsToExchangeWithDeck());
        assertEquals(bot2, bot1.playerToExchangeCards(bot1.getPlayers()));
        assertEquals(Action.EXCHANGE_PLAYER, bot1.nextAction());
        bot2.removeFromHand(List.of(new DragonGate(), new Docks(), new Laboratory()));
        assertNull(bot1.playerToExchangeCards(bot1.getPlayers()));
        bot1.removeAction(Action.EXCHANGE_DECK);
        assertEquals(Action.NONE, bot1.nextAction());
    }

    @Test
    void getMostDangerousPlayersByBuiltDistrictsTest() {
        Bot kingBot = new Bot("bot 1", 10, List.of(new Temple())) {
            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }

            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new King()) ? new King() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot merchantBot = new Bot("bot 2", 15, List.of(new Manor(), new Harbor())) {
            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }

            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Merchant()) ? new Merchant() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot bishopBot = new Bot("bot 3", 20, List.of(new Church(), new Monastery(), new Cathedral(), new Fortress())) {
            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }

            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Bishop()) ? new Bishop() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot warlordBot = new Bot("bot 4", 50, List.of(new Castle(), new Library(), new Tavern(), new TownHall())) {
            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }

            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Game game = new Game(kingBot, merchantBot, bishopBot, warlordBot);
        game.characterSelectionTurn();
        game.playerTurn(kingBot);
        game.playerTurn(merchantBot);
        game.playerTurn(merchantBot);
        game.playerTurn(bishopBot);
        game.playerTurn(bishopBot);
        game.playerTurn(bishopBot);
        game.playerTurn(warlordBot);
        game.playerTurn(warlordBot);
        game.playerTurn(warlordBot);
        game.playerTurn(warlordBot);
        assertEquals(1, kingBot.getBuiltDistricts().size());
        assertEquals(2, merchantBot.getBuiltDistricts().size());
        assertEquals(3, bishopBot.getBuiltDistricts().size());
        assertEquals(4, warlordBot.getBuiltDistricts().size());
        assertEquals(List.of(warlordBot, bishopBot, merchantBot), kingBot.getMostDangerousPlayersByBuiltDistricts(game.getIPlayerList()));
        assertEquals(List.of(bishopBot, merchantBot, kingBot), warlordBot.getMostDangerousPlayersByBuiltDistricts(game.getIPlayerList()));
        assertEquals(List.of(warlordBot, bishopBot, kingBot), merchantBot.getMostDangerousPlayersByBuiltDistricts(game.getIPlayerList()));
        game.playerTurn(bishopBot);
        // As the warlordBot has more purple district built than bishopBot, he should be first in dangerousness level
        assertEquals(List.of(warlordBot, bishopBot, merchantBot), kingBot.getMostDangerousPlayersByBuiltDistricts(game.getIPlayerList()));
    }

    @Test
    void destroyDistrictTest() {
        Bot warlordBot = new Bot("warlordBot", 50, Collections.emptyList()) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot bishopBot = new Bot("bishopBot", 50, List.of(new TheKeep(), new Temple())) {
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
                return Action.NONE;
            }
        };
        Bot merchantBot = new Bot("merchantBot", 50, List.of(new Harbor(), new Temple(), new Church())) {
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
                return Action.NONE;
            }
        };
        Bot kingBot = new Bot("kingBot", 50, List.of(new University(), new Harbor(), new Prison(), new Docks())) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new King()) ? new King() : availableCharacters.get(0);
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
                return Action.NONE;
            }
        };
        // Here, we force bishopBot and bot3 to take the income, so they can only build the district in their hands
        Game game = new Game(warlordBot, bishopBot, merchantBot, kingBot);
        game.characterSelectionTurn();
        game.playerTurn(bishopBot);
        game.playerTurn(bishopBot);
        assertNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(merchantBot);
        game.playerTurn(merchantBot);
        game.playerTurn(merchantBot);
        assertEquals(List.of(merchantBot, bishopBot, kingBot), warlordBot.getMostDangerousPlayersByBuiltDistricts(game.getIPlayerList()));
        assertEquals(new SimpleEntry<>(merchantBot, new Harbor()), warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(kingBot);
        game.playerTurn(kingBot);
        game.playerTurn(kingBot);
        game.playerTurn(kingBot);
        var districtToDestroy = warlordBot.destroyDistrict(game.getIPlayerList());
        assertEquals(new SimpleEntry<>(kingBot, new University()), districtToDestroy);
    }

    @Test
    void removeDistrictFromDistrictBuiltTest() {
        Bot bot1 = new Bot("bot1", 10, List.of(new Temple())) {
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

            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new King()) ? new King() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Game game = new Game(bot1);
        game.characterSelectionTurn();
        game.playerTurn(bot1);
        assertEquals(1, bot1.getBuiltDistricts().size());
        bot1.removeDistrictFromDistrictBuilt(bot1.getBuiltDistricts().get(0));
        assertEquals(0, bot1.getBuiltDistricts().size());
    }

    @Test
    void destroyDistrictTest2() {
        Bot warlordBot = new Bot("warlordBot", 1, Collections.emptyList()) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
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
                return Action.NONE;
            }
        };
        Bot merchantBot = new Bot("merchantBot", 50, List.of(new Observatory(), new HauntedCity(), new Temple(), new Church(), new Harbor())) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Merchant()) ? new Merchant() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }

            @Override
            protected double districtProfitability(District district) {
                return getHandDistricts().indexOf(district);
            } // To control the districts the bot will build

            @Override
            public Action nextAction(Set<Action> remainingActions) {
                var objective = districtObjective();
                if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
                    return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
                if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
                    return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
                return Action.NONE;
            }
        };
        Game game = new Game(merchantBot, warlordBot);
        game.characterSelectionTurn();
        assertNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(merchantBot);
        assertNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(merchantBot);
        assertNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(merchantBot);
        assertEquals(new SimpleEntry<>(merchantBot, new Temple()), warlordBot.destroyDistrict(game.getIPlayerList()));
        warlordBot.gainCoins(3);
        assertEquals(4, warlordBot.getCoins());
        game.playerTurn(merchantBot);
        assertEquals(new SimpleEntry<>(merchantBot, new HauntedCity()), warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(merchantBot);
        assertEquals(new SimpleEntry<>(merchantBot, new HauntedCity()), warlordBot.destroyDistrict(game.getIPlayerList()));
        warlordBot.gainCoins(1);
        assertEquals(5, warlordBot.getCoins());
        assertEquals(new SimpleEntry<>(merchantBot, new Observatory()), warlordBot.destroyDistrict(game.getIPlayerList()));

    }

    @Test
    void cardToDiscardTest() {
        assertEquals(new Church(), bot1.cardToDiscard());
        assertEquals(Action.DISCARD, bot1.nextAction(Set.of(Action.DISCARD)));
        bot1.gainCoins(14);
        bot1.getHandDistricts().forEach(d -> bot1.buildDistrict(d, 0));
        assertNotEquals(Action.DISCARD, bot1.nextAction(Set.of(Action.DISCARD)));
        bot1.addDistrictToHand(new Manor());
        assertNotEquals(Action.DISCARD, bot1.nextAction(Set.of(Action.DISCARD)));
    }

    @Test
    void cardToDiscard() {
        bot1.gainCoins(1000);
        bot1.getHandDistricts().forEach(d -> bot1.buildDistrict(d, 0));
        System.out.println(bot1.getCoins());
        assertEquals(Action.TAKE_THREE, bot1.nextAction(Set.of(Action.TAKE_THREE)));
        bot1.pay(987);
        assertNotEquals(Action.TAKE_THREE, bot1.nextAction(Set.of(Action.TAKE_THREE)));
        bot1.gainCoins(1000);
        bot1.addDistrictToHand(new Manor());
        assertNotEquals(Action.TAKE_THREE, bot1.nextAction(Set.of(Action.TAKE_THREE)));
    }

    @Test
    void canDestroyTest() {
        Bot warlordBot = new Bot("warlordBot", 20, Collections.emptyList()) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot bot2 = new Bot("bot 2", 10, List.of(new WatchTower())) {
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
                return Action.NONE;
            }
        };
        Bot bot3 = new Bot("bot 3", 10, List.of(new TheKeep())) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new King()) ? new King() : availableCharacters.get(0);
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
                return Action.NONE;
            }
        };
        Bot bishopBot = new Bot("bishopBot", 10, List.of(new Harbor(), new Temple())) {
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
                return Action.NONE;
            }
        };
        Game game = new Game(warlordBot, bot3, bot2, bishopBot);
        game.characterSelectionTurn();
        assertNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(warlordBot);
        game.playerTurn(bot3); // bot3 builds a non-destroyable district
        assertNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        game.playerTurn(bishopBot);
        assertNull(warlordBot.destroyDistrict(game.getIPlayerList())); // bishopBot can't get attacked
        game.playerTurn(bot2);
        assertNotNull(warlordBot.destroyDistrict(game.getIPlayerList()));
    }

    @Test
    void canDestroyTest2() {
        Bot warlordBot = new Bot("bot 1", 10, Collections.emptyList()) {
            @Override
            public Character pickCharacter(List<Character> availableCharacters) {
                Character best = availableCharacters.contains(new Warlord()) ? new Warlord() : availableCharacters.get(0);
                setCharacter(best);
                return best;
            }
        };
        Bot merchantBot = new Bot("merchantBot", 50, List.of(new Church(), new Monastery(), new Harbor(), new Castle(),
                new Temple(), new University(), new WatchTower(), new Tavern(), new Smithy())) {
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
        Game game = new Game(warlordBot, merchantBot);
        game.characterSelectionTurn();
        for (int i = 1; i < 8; i++) {
            game.playerTurn(merchantBot);
            assertNotNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        }
        for (int i = 1; i < 3; i++) {
            game.playerTurn(merchantBot);
            assertNull(warlordBot.destroyDistrict(game.getIPlayerList()));
        }
    }
}