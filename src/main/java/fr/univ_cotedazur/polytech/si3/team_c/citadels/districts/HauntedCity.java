package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

import java.util.Optional;

public class HauntedCity extends District {
    public HauntedCity() {
        super("Haunted City", 2, Colors.PURPLE, 2);
    }

    @Override
    public Optional<Colors> bonusColors(boolean builtInLastTurn) {
        if (builtInLastTurn) return Optional.of(getColor());
        return Optional.empty();
    }
}
