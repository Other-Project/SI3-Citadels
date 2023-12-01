package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.characters.Character;

import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Rebot player
 *
 * @author Team C
 */
public class Bot extends Player {
    Random random;

    public Bot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
        random = new Random();
    }

    @Override
    public Character pickCharacter(List<Character> availableCharacters) {
        setCharacter(availableCharacters.get(random.nextInt(availableCharacters.size())));
        return getCharacter();
    }

    @Override
    public boolean pickCoins() {
        var minCost = getHandDistricts().stream().mapToInt(District::getCost).min(); // For now, picks coins only if nothing can be built
        return minCost.isEmpty() || getCoins() < minCost.getAsInt();
    }

    @Override
    public List<District> pickDistrictsToBuild() {
        for (District district : getHandDistricts()) {
            if (buildDistricts(district))
                return List.of(district); // For now builds the first district found buildable
        }
        return Collections.emptyList();
    }
}
