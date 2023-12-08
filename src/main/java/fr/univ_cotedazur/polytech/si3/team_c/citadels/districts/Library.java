package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

public class Library extends District {
    public Library() {
        super("Library", 6, Colors.PURPLE, 6);
    }

    @Override
    public int numberOfCardsToKeep() {
        return 2;
    }
}
