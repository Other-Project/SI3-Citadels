package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

public class TheKeep extends District {
    public TheKeep() {
        super("The Keep", 3, Colors.PURPLE, 3);
    }

    @Override
    public boolean isDestructible() {
        return false;
    }
}
