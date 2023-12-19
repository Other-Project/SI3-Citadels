package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public enum Action {
    /**
     * The player receives a certain amount of coins
     */
    INCOME,
    /**
     * Bonus income based on the color of the player's character and on the districts built
     */
    SPECIAL_INCOME,
    /**
     * The player draws districts and chooses to keep a certain amount of them
     */
    DRAW,
    /**
     * The player wants to build a district from his hand
     */
    BUILD,
    /**
     * The player wants to rob a character. When his turn begins, the player will take all his gold
     */
    STEAL,
    /**
     * The player want exchange some of his cards with the deck
     */
    EXCHANGE_DECK,
    /**
     * The player want exchange all his cards with the cards of another player
     */
    EXCHANGE_PLAYER,
    /**
     * The player wants to end his turn
     */
    NONE
}
