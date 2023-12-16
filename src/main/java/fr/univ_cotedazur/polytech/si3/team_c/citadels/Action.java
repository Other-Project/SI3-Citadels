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
     * The player want to discard a card to gain a coin
     */
    DISCARD,
    /**
     * The player take 3 cards and pay 3 coins
     */
    TAKE_THREE,
    /**
     * The player wants to end his turn
     */
    NONE
}
