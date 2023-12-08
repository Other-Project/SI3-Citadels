package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

public class Observatory extends District {
    public Observatory() {
        super("Observatory", 5, Colors.PURPLE, 5);
    }

    @Override
    public int numberOfCardsToDraw() {
        return 3;
    }
}
