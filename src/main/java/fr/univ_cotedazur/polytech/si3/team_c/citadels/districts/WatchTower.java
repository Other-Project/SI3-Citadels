package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

public class WatchTower extends District {
    public WatchTower() {
        super("Watch Tower", 1, Colors.RED, 1);
    }

    @Override
    public boolean isDestructible() {
        return false;
    }
}
