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
     * The player wants to rob a character. When his turn become, the player will take all his gold
     */
    STEAL,
    /**
     * The player wants to end his turn
     */
    NONE
}
