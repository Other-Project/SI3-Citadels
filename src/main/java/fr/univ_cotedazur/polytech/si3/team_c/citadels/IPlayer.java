package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.List;
import java.util.Optional;

public interface IPlayer {
    /**
     * Gets the name of the player
     */
    String getName();

    /**
     * Gets the amount of coins the player have
     */
    int getCoins();

    /**
     * Gets the amount of cards the player have in hand
     */
    int getHandSize();

    /**
     * Gets all the districts that the player built (and that haven't been destroyed)
     */
    List<District> getBuiltDistricts();

    /**
     * Gets all the destroyable districts that the player built
     */
    List<District> getDestroyableDistricts();

    /**
     * Gets the chosen character (can be empty if the player hasn't chosen yet)
     */
    Optional<Character> getCharacter();

    /**
     * Gets if the current player has the crown
     */
    boolean hasCrown();
}
