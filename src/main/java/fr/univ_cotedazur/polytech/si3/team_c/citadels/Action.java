package fr.univ_cotedazur.polytech.si3.team_c.citadels;

public enum Action {
    /**
     * The Default Income : the player can choose this action to get 2 coins
     */
    INCOME,
    /**
     * It's a bonus incoe according to the player's character
     */
    SPECIAL_INCOME,
    /**
     * The default drawing : the player chooses a card between two from the deck
     */
    DRAW,
    /**
     * The build action : the player builds a district from his hand according to its cost
     */
    BUILD,
    /**
     * The action of stopping the layer's turn
     */
    NONE
}
