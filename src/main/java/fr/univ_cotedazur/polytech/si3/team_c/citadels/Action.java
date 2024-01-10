package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public enum Action {
    /**
     * The player receives a certain amount of coins
     */
    INCOME("claim his income") {
        @Override
        public String doAction(Player player) {
            return MessageFormat.format("{0} got {1} coins", player.getName(), player.gainIncome());
        }
    },
    /**
     * Bonus income based on the color of the player's character and on the districts built
     */
    SPECIAL_INCOME("claim his special income") {
        @Override
        public String doAction(Player player) {
            int claimedCoins = player.gainSpecialIncome();
            return MessageFormat.format("{0} got {1} coins", player.getName(), Integer.toString(claimedCoins));
        }
    },
    /**
     * The player draws districts and chooses to keep a certain amount of them
     */
    DRAW("draw districts", INCOME) {
        @Override
        public String doAction(Player player) {
            var drawnCard = player.getGameStatus().draw(player.numberOfDistrictsToDraw());
            LOGGER.log(Level.INFO, "{0} drew {1}", new Object[]{player.getName(), drawnCard});
            player.pickDistrictsFromDeck(drawnCard)
                    .forEach(district -> LOGGER.log(Level.INFO, "{0} kept {1}", new Object[]{player.getName(), district}));
        }
    },
    /**
     * The player wants to build a district from his hand
     */
    BUILD("build district(s)") {
        @Override
        public String doAction(Player player) {
            player.pickDistrictsToBuild(currentTurn)
                    .forEach(district -> LOGGER.log(Level.INFO, "{0} built {1}", new Object[]{player.getName(), district}));
        }
    },
    /**
     * The player wants to discard a card to gain a coin
     */
    DISCARD("discard a card in order to receive a coin") {
        @Override
        public String doAction(Player player) {
            District card = player.cardToDiscard();
            player.getHandDistricts().remove(card); // If no card chose the player would not be able to do this action
            player.gainCoins(1);
            return MessageFormat.format("{0} discarded {1} in order to received one coin", player.getName(), card);
        }
    },
    /**
     * The player takes 3 cards and pays 3 coins
     */
    TAKE_THREE("pay 3 coins in order to draw 3 cards") {
        @Override
        public String doAction(Player player) {
            List<District> drawnCards = player.getGameStatus().draw(3);
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
        public String doAction(Player player) {
            characterToRob = player.chooseCharacterToRob(charactersToInteractWith);
            LOGGER.log(Level.INFO, "{0} tries to steal the {1}", new Object[]{player.getName(), characterToRob});
            robber = player;
        }
    },
    /**
     * The player wants to kill a character. When a character has been killed, he can't play anymore
     */
    KILL("kill a character") {
        @Override
        public String doAction(Player player) {
            if (charactersToInteractWith.isEmpty()) return;
            characterToKill = player.chooseCharacterToKill(charactersToInteractWith);
            LOGGER.log(Level.INFO, "{0} kills the {1}", new Object[]{player.getName(), characterToKill});
        }
    },
    /**
     * The player wants to exchange some of his cards with the deck
     */
    EXCHANGE_DECK("exchange some of his cards with the deck") {
        @Override
        public String doAction(Player player) {
            List<District> cardsToExchange = player.chooseCardsToExchangeWithDeck();
            assert (!cardsToExchange.isEmpty());
            discard.addAll(cardsToExchange);
            player.removeFromHand(cardsToExchange);
            List<District> cards = player.getGameStatus().draw(cardsToExchange.size());
            cards.forEach(player::addDistrictToHand);
            return MessageFormat.format("{0} exchanges some cards {1} with the deck, he got {2}", player.getName(), cardsToExchange, cards);
        }
    },
    /**
     * The player wants to exchange all his cards with the cards of another player
     */
    EXCHANGE_PLAYER("exchange his hand with the hand of another player", EXCHANGE_DECK) {
        @Override
        public String doAction(Player player) {
            String playerToExchangeCards = player.choosePlayerToExchangeCards(player.getGameStatus().getCardsNumber());
            List<District> hand1 = player.getHandDistricts();
            List<District> handExchange = playerToExchangeCards.getHandDistricts();
            player.removeFromHand(hand1);
            playerToExchangeCards.removeFromHand(handExchange);
            hand1.forEach(playerToExchangeCards::addDistrictToHand);
            handExchange.forEach(player::addDistrictToHand);
            return MessageFormat.format("{0} exchanges his cards {1} with {2}, he got {3}", player.getName(), hand1, playerToExchangeCards, handExchange);
        }
    },
    /**
     * The player wants to destroy a district
     */
    DESTROY("destroy a district") {
        @Override
        public String doAction(Player player) {
            Map<String, List<District>> districtListToDestroyFrom = player.getGameStatus().getDistrictListToDestroyFrom();
            AbstractMap.SimpleEntry<String, District> districtToDestroy = player.destroyDistrict(districtListToDestroyFrom).orElseThrow();
            Player playerToTarget = linkStringToPlayer(districtToDestroy.getKey());
            playerToTarget.removeDistrictFromDistrictBuilt(districtToDestroy.getValue());
            player.pay(districtToDestroy.getValue().getCost() - 1);
            return MessageFormat.format("{0} destroys the {1} of {2}\n{0} has now {3} coins",
                    player.getName(), districtToDestroy.getValue(), playerToTarget.getName(), player.getCoins());
        }
    },
    /**
     * The player draws 2 districts at the beginning of his turn
     */
    BEGIN_DRAW,
    /**
     * The player gets a coin at the game startup
     */
    STARTUP_INCOME,
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

    public String doAction(Player player) {
        return null;
    }

    public String getDescription() {
        return description;
    }

    public Set<Action> getIncompatibleActions() {
        return incompatibleActions;
    }
}
