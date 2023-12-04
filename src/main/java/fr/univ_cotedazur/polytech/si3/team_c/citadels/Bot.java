package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Rebot player
 *
 * @author Team C
 */
public class Bot extends Player {
    private final Random random;

    public Bot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
        random = new Random();
    }

    @Override
    public Character pickCharacter(List<Character> availableCharacters) {
        setCharacter(availableCharacters.get(random.nextInt(availableCharacters.size())));
        return getCharacter().orElseThrow();
    }

    @Override
    public Action nextAction(List<Action> remainingActions) {
        var minCost = getHandDistricts().stream().mapToInt(District::getCost).min();
        var canBuild = minCost.isPresent() && getCoins() > minCost.getAsInt();
        if (remainingActions.contains(Action.INCOME) && minCost.isPresent() && !canBuild)
            return Action.INCOME; // For now, picks coins only if nothing can be built
        if (remainingActions.contains(Action.DRAW)) return Action.DRAW;
        if (remainingActions.contains(Action.BUILD) && canBuild) return Action.BUILD;
        return Action.NONE;
    }

    @Override
    public List<District> pickDistrictsFromDeck(List<District> drawnCards, int amountToChoose) {
        ArrayList<District> chosen = new ArrayList<>(drawnCards.stream()
                .filter(c -> !getBuiltDistricts().contains(c))
                .filter(c -> !getHandDistricts().contains(c))
                .limit(amountToChoose).toList()); // For now, we select the first x districts
        if (chosen.size() < amountToChoose && drawnCards.size() > chosen.size())
            chosen.addAll(drawnCards.stream().filter(c -> !chosen.contains(c))
                    .limit((long) amountToChoose - chosen.size()).toList());
        chosen.forEach(this::addDistrictToHand);
        return chosen;
    }

    @Override
    public List<District> pickDistrictsToBuild(int maxAmountToChoose) {
        ArrayList<District> built = new ArrayList<>();
        for (District district : getHandDistricts()) {
            if (maxAmountToChoose > 0 && buildDistrict(district)) { // For now builds the first x districts found buildable
                built.add(district);
                maxAmountToChoose--;
            }
        }
        return built;
    }
}
