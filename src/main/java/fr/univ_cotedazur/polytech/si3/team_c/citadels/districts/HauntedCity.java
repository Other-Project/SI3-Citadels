package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

import java.util.List;

public class HauntedCity extends District {
    public HauntedCity() {
        super("Haunted City", 2, Colors.PURPLE, 2);
    }

    @Override
    public List<Colors> bonusColors() {
        return List.of(Colors.GREEN, Colors.BLUE, Colors.YELLOW, Colors.RED, Colors.PURPLE);
    }
}
