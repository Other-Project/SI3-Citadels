package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.HashSet;
import java.util.Set;

public enum Action {
    /**
     * The player receives a certain amount of coins
     */
    INCOME("claim his income"),
    /**
     * Bonus income based on the color of the player's character and on the districts built
     */
    SPECIAL_INCOME("claim his special income"),
    /**
     * The player draws districts and chooses to keep a certain amount of them
     */
    DRAW("draw districts", INCOME),
    /**
     * The player wants to build a district from his hand
     */
    BUILD("build district(s)"),
    /**
     * The player wants to discard a card to gain a coin
     */
    DISCARD("discard a card in order to receive a coin"),
    /**
     * The player takes 3 cards and pays 3 coins
     */
    TAKE_THREE("pay 3 coins in order to draw 3 cards"),
    /**
     * The player wants to rob a character. When his turn begins, the player will take all his gold
     */
    STEAL("steal a character"),
    /**
     * The player wants to kill a character. When a character has been killed, he can't play anymore
     */
    KILL("kill a character"),
    /**
     * The player wants to exchange some of his cards with the deck
     */
    EXCHANGE_DECK("exchange some of his cards with the deck"),
    /**
     * The player wants to exchange all his cards with the cards of another player
     */
    EXCHANGE_PLAYER("exchange his hand with the hand of another player", EXCHANGE_DECK),
    /**
     * The player wants to destroy a district
     */
    DESTROY("destroy a district"),
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

    public String getDescription() {
        return description;
    }

    public Set<Action> getIncompatibleActions() {
        return incompatibleActions;
    }
}
