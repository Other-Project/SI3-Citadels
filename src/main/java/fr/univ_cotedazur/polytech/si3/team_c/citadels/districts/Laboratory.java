package fr.univ_cotedazur.polytech.si3.team_c.citadels.districts;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Action;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.Colors;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.District;

import java.util.List;

public class Laboratory extends District {
    public Laboratory() {
        super("Laboratory", 5, Colors.PURPLE, 5);
    }

    /**
     * Method to return the special action of the district Laboratory
     *
     * @return an optional with a list which contains the action DISCARD
     */
    @Override
    public List<Action> getAction() {
        return List.of(Action.DISCARD);
    }
}
