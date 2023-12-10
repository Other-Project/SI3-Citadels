package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.*;


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

    /**
     * Calculate a profitability score for a given district
     *
     * @param district The district whose profitability is to be calculated
     */
    protected double districtProfitability(District district) {
        return district.getPoint() / (double) district.getCost();
    }

    /**
     * The district that the bot aims to build next
     *
     * @return Empty if no district in hand
     */
    protected Optional<District> districtObjective() {
        District bestDistrict = null;
        double bestProfitability = 0;
        for (District district : getHandDistricts()) {
            double profitability = districtProfitability(district);
            if (profitability > bestProfitability || (bestDistrict != null && profitability == bestProfitability && district.getCost() < bestDistrict.getCost())) {
                bestDistrict = district;
                bestProfitability = profitability;
            }
        }
        return Optional.ofNullable(bestDistrict);
    }

    @Override
    public Action nextAction(List<Action> remainingActions) {
        var objective = districtObjective();
        if (remainingActions.contains(Action.INCOME) && objective.isPresent() && objective.get().getCost() > getCoins())
            return Action.INCOME; // Pick coins if the bot has an objective and the objective cost more than what he has
        if (remainingActions.contains(Action.DRAW)) return Action.DRAW;
        if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
            return Action.BUILD;
        return Action.NONE;
    }

    @Override
    public List<District> pickDistrictsFromDeck(List<District> drawnCards, int amountToChoose) {
        ArrayList<District> chosen = new ArrayList<>(drawnCards.stream()
                .filter(c -> !getBuiltDistricts().contains(c))
                .filter(c -> !getHandDistricts().contains(c))
                .sorted(Comparator.comparingDouble(this::districtProfitability).reversed())
                .limit(amountToChoose).toList());
        if (chosen.size() < amountToChoose && drawnCards.size() > chosen.size())
            chosen.addAll(drawnCards.stream().filter(c -> !chosen.contains(c))
                    .limit((long) amountToChoose - chosen.size()).toList());
        chosen.forEach(this::addDistrictToHand);
        return chosen;
    }

    @Override
    public List<District> pickDistrictsToBuild(int maxAmountToChoose) {
        ArrayList<District> built = new ArrayList<>();
        for (; maxAmountToChoose > 0; maxAmountToChoose--) {
            var objective = districtObjective();
            if (objective.isEmpty() || !buildDistrict(objective.get()))
                break;
            built.add(objective.get());
        }

        return built;
    }
}
