package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.Player;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Action {
    /**
     * The player receives a certain amount of coins
     */
    INCOME("claim his income") {
        @Override
        public String doAction(Game game, Player player) {
            return MessageFormat.format("{0} got {1} coins", player.getName(), player.gainIncome());
        }
    },
    /**
     * Bonus income based on the color of the player's character and on the districts built
     */
    SPECIAL_INCOME("claim his special income") {
        @Override
        public String doAction(Game game, Player player) {
            return MessageFormat.format("{0} got {1} coins", player.getName(), player.gainSpecialIncome());
        }
    },
    /**
     * The player draws districts and chooses to keep a certain amount of them
     */
    DRAW("draw districts", INCOME) {
        @Override
        public String doAction(Game game, Player player) {
            var drawnCard = game.getDeck().draw(player.numberOfDistrictsToDraw());
            List<District> districtsToKeep = player.pickDistrictsFromDeck(drawnCard);
            game.getDeck().addAll(drawnCard.stream().filter(
                    district -> {
                        for (District card : districtsToKeep) {
                            if (card == district) return true;
                        }
                        return false;
                    }).toList()); // We add back to the deck the districts that the player doesn't want to keep
            return MessageFormat.format("{0} kept {1}", player.getName(), districtsToKeep);
        }
    },
    /**
     * The player wants to build a district from his hand
     */
    BUILD("build district(s)") {
        @Override
        public String doAction(Game game, Player player) {
            List<District> districtToBuild = player.pickDistrictsToBuild(game.getCurrentTurn());
            districtToBuild.forEach(district -> district.getEventAction().forEach(a -> game.registerPlayerForEventAction(player, a)));
            return MessageFormat.format("{0} built {1}", player.getName(), districtToBuild);
        }
    },
    /**
     * The player wants to discard a card to gain a coin
     */
    DISCARD("discard a card in order to receive a coin") {
        @Override
        public String doAction(Game game, Player player) {
            District card = player.cardToDiscard();
            player.removeFromHand(List.of(card)); // If no card chose the player would not be able to do this action
            game.getDeck().addLast(card);
            player.gainCoins(1);
            return MessageFormat.format("{0} discarded {1} in order to received one coin", player.getName(), card);
        }
    },
    /**
     * The player takes 3 cards and pays 3 coins
     */
    TAKE_THREE("pay 3 coins in order to draw 3 cards") {
        @Override
        public String doAction(Game game, Player player) {
            List<District> drawnCards = game.getDeck().draw(3);
            player.pay(3);
            drawnCards.forEach(player::addDistrictToHand);
            return MessageFormat.format("{0} payed 3 coins in order to received: {1}", player.getName(), drawnCards);
        }
    },
    /**
     * The player wants to rob a character. When his turn begins, the player will take all his gold
     */
    STEAL("steal a character") {
        @Override
        public String doAction(Game game, Player player) {
            if (game.getCharactersToInteractWith().isEmpty()) return null; // Useful for tests
            Character characterToRob = player.chooseCharacterToRob(game.getCharactersToInteractWith());
            game.performActionOnCharacter(characterToRob, player, SufferedActions.STOLEN);
            return MessageFormat.format("{0} tries to steal the {1}", player.getName(), characterToRob);
        }
    },
    /**
     * The player wants to kill a character. When a character has been killed, he can't play anymore
     */
    KILL("kill a character") {
        @Override
        public String doAction(Game game, Player player) {
            if (game.getCharactersToInteractWith().isEmpty()) return null; // Useful for tests
            Character characterToKill = player.chooseCharacterToKill(game.getCharactersToInteractWith());
            game.performActionOnCharacter(characterToKill, player, SufferedActions.KILLED);
            return MessageFormat.format("{0} kills the {1}", player.getName(), characterToKill);
        }
    },
    /**
     * The player wants to exchange some of his cards with the deck
     */
    EXCHANGE_DECK("exchange some of his cards with the deck") {
        @Override
        public String doAction(Game game, Player player) {
            List<District> cardsToExchange = player.chooseCardsToExchangeWithDeck();
            assert (!cardsToExchange.isEmpty());
            game.getDeck().addAll(cardsToExchange);
            player.removeFromHand(cardsToExchange);
            List<District> cards = game.getDeck().draw(cardsToExchange.size());
            cards.forEach(player::addDistrictToHand);
            return MessageFormat.format("{0} exchanges some cards {1} with the deck, he got {2}", player.getName(), cardsToExchange, cards);
        }
    },
    /**
     * The player wants to exchange all his cards with the cards of another player
     */
    EXCHANGE_PLAYER("exchange his hand with the hand of another player", EXCHANGE_DECK) {
        @Override
        public String doAction(Game game, Player player) {
            Player playerToExchangeCards = (Player) player.playerToExchangeCards(player.getPlayers());
            List<District> hand1 = player.getHandDistricts();
            List<District> handExchange = playerToExchangeCards.getHandDistricts();
            player.removeFromHand(hand1);
            playerToExchangeCards.removeFromHand(handExchange);
            hand1.forEach(playerToExchangeCards::addDistrictToHand);
            handExchange.forEach(player::addDistrictToHand);
            return MessageFormat.format("{0} exchanges his cards {1} with {2}, he got {3}", player.getName(), hand1, playerToExchangeCards.getName(), handExchange);
        }
    },
    /**
     * The player wants to destroy a district
     */
    DESTROY("destroy a district") {
        @Override
        public String doAction(Game game, Player player) {
            AbstractMap.SimpleEntry<IPlayer, District> districtToDestroy = player.destroyDistrict(game.getIPlayerList());
            ((Player) districtToDestroy.getKey()).removeDistrictFromDistrictBuilt(districtToDestroy.getValue());
            player.pay(districtToDestroy.getValue().getCost() - 1);

            List<Action> actions = districtToDestroy.getValue().getEventAction();
            actions.forEach(action -> game.unregisterPlayerForEventAction(player, action));
            game.callEventAction(Action.RECOVER_DESTROYED_DISTRICT, player, districtToDestroy.getValue());

            return MessageFormat.format("{0} destroys the {1} of {2}\n{0} has now {3} coins",
                    player.getName(), districtToDestroy.getValue(), districtToDestroy.getKey().getName(), player.getCoins());
        }
    },
    /**
     * The player draws 2 districts at the beginning of his turn
     */
    BEGIN_DRAW("draws") {
        @Override
        public String doAction(Game game, Player player) {
            var drawnCards = game.getDeck().draw(2);
            drawnCards.forEach(player::addDistrictToHand);
            return MessageFormat.format("{0} drew 2 extra districts {1} because he was the {2}", player.getName(), drawnCards, player.getCharacter().orElseThrow());
        }
    },
    /**
     * The player gets a coin at the game startup
     */
    STARTUP_INCOME {
        @Override
        public String doAction(Game game, Player player) {
            player.gainCoins(1);
            return MessageFormat.format("{0} earned a coin because he was the {1}", player.getName(), player.getCharacter().orElseThrow());
        }
    },
    /**
     * The player pays one coin to get a district that has been destroyed during the turn
     */
    RECOVER_DESTROYED_DISTRICT {
        @Override
        public <T> String doEventAction(Game game, Player caller, Player eventPlayer, T param) {
            if (!(param instanceof District districtToDestroy)) return null;
            if (!eventPlayer.equals(caller)
                    && eventPlayer.wantsToTakeADestroyedDistrict(districtToDestroy)) {
                eventPlayer.pay(1);
                eventPlayer.addDistrictToHand(districtToDestroy);
                return MessageFormat.format("{0} payed one coin to recover {1}", eventPlayer.getName(), districtToDestroy);
            } else game.getDeck().add(districtToDestroy);
            return null;
        }
    },
    /**
     * The player gets the crown at the beginning of his turn
     */
    GET_CROWN {
        @Override
        public String doAction(Game game, Player player) {
            game.setCrown(player);
            return MessageFormat.format("{0} got the crown because he was the {1}", player.getName(), player.getCharacter().orElseThrow());
        }
    },
    /**
     * The player wants to end his turn
     */
    NONE;


    private final String description;
    private final Set<Action> incompatibleActions;

    Action() {
        this(null);
    }

    Action(String description, Action... incompatibleActions) {
        this.description = description;
        this.incompatibleActions = new HashSet<>(Set.of(incompatibleActions));
        for (Action incompatibleAction : incompatibleActions) incompatibleAction.incompatibleActions.add(this);
    }

    public String doAction(Game game, Player player) {
        return null;
    }

    public <T> String doEventAction(Game game, Player caller, Player eventPlayer, T param) {
        return null;
    }

    public String getDescription() {
        return description;
    }

    public Set<Action> getIncompatibleActions() {
        return incompatibleActions;
    }
}
