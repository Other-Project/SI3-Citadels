package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.*;

/**
 * Rebot player
 *
 * @author Team C
 */
public class Bot extends Player {

    public Bot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }

    @Override
    public Character pickCharacter(List<Character> availableCharacters) {
        Character best = null;
        int profitability = -1;
        for (Character character : availableCharacters) {
            if (quantityOfColorBuilt(character.getColor()) > profitability)
                best = character;
        }
        setCharacter(best);
        return best;
    }

    /**
     * Calculate a profitability score for a given district
     *
     * @param district The district whose profitability is to be calculated
     */
    protected double districtProfitability(District district) {
        return district.getPoint()
                + quantityOfColorBuilt(district.getColor()) / 8.0
                - district.getCost();
    }

    /**
     * The district that the bot aims to build next
     *
     * @return Empty if no district in hand
     */
    protected Optional<District> districtObjective() {
        District bestDistrict = null;
        double bestProfitability = Double.MIN_VALUE;
        for (District district : getHandDistricts()) {
            double profitability = districtProfitability(district);
            if (bestDistrict == null || profitability > bestProfitability || (profitability == bestProfitability && district.getCost() < bestDistrict.getCost())) {
                bestDistrict = district;
                bestProfitability = profitability;
            }
        }
        return Optional.ofNullable(bestDistrict);
    }

    @Override
    public Action nextAction(List<Action> remainingActions) {
        var objective = districtObjective();
        if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
            return Action.INCOME; // Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
        if (remainingActions.contains(Action.DRAW)) return Action.DRAW;
        if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
            return Action.BUILD;
        if (remainingActions.contains(Action.SPECIAL_INCOME) && quantityOfColorBuilt(getCharacter().orElseThrow().getColor()) > 0)
            return Action.SPECIAL_INCOME;
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
    public List<District> pickDistrictsToBuild(int maxAmountToChoose, int turn) {
        ArrayList<District> built = new ArrayList<>();
        for (; maxAmountToChoose > 0; maxAmountToChoose--) {
            var objective = districtObjective();
            if (objective.isEmpty() || !buildDistrict(objective.get(), turn))
                break;
            built.add(objective.get());
        }

        return built;
    }

    @Override
    public void pickBonusColor(Set<Colors> tookColors) {
        List<Colors> colors = new ArrayList<>(Arrays.asList(Colors.values()));
        colors.remove(Colors.NONE);
        int i = 0;
        while (i < colors.size() && tookColors.contains(colors.get(i))) i++;
        if (!tookColors.contains(colors.get(i))) tookColors.add(colors.get(i));
    }
}
