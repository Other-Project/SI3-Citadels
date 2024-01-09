package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

/**
 * Robot player
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
        double maxProfitability = -1;
        for (Character character : availableCharacters) {
            var profitability = characterProfitability(character);
            if (profitability <= maxProfitability) continue;
            best = character;
            maxProfitability = profitability;
        }
        setCharacter(best);
        return best;
    }

    /**
     * Calculate a profitability score for a given character
     *
     * @param character The character whose profitability is to be calculated
     */
    protected double characterProfitability(Character character) {
        return quantityOfColorBuilt(character.getColor());
    }

    /**
     * Calculates the gain on a property from the construction of a district compared to the current one
     *
     * @param district         The district in question
     * @param districtProperty Getter of the property on the district side
     * @param playerProperty   Getter of the property on the player side
     * @return The difference between the two
     */
    private double districtPropertyGain(District district, Function<District, Optional<? extends Number>> districtProperty, DoubleSupplier playerProperty) {
        return districtProperty.apply(district)
                .map(aDouble -> (aDouble.doubleValue() - playerProperty.getAsDouble()))
                .orElse(0.0);
    }

    /**
     * Calculate a profitability score for a given district
     *
     * @param district The district whose profitability is to be calculated
     */
    protected double districtProfitability(District district) {
        return district.getPoint()
                + quantityOfColorBuilt(district.getColor()) / 8.0
                + districtPropertyGain(district, District::numberOfDistrictsToDraw, this::numberOfDistrictsToDraw) / (getBuiltDistricts().size() + 1)
                + districtPropertyGain(district, District::numberOfDistrictsToKeep, this::numberOfDistrictsToKeep) / (getBuiltDistricts().size() + 1)
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

    /**
     * Gets the district that the bot wants to discard
     *
     * @return The card to discard to gain one coin
     */
    @Override
    public District cardToDiscard() {
        assert (!getHandDistricts().isEmpty());
        District worst = getHandDistricts().get(0);
        for (District d : getHandDistricts()) if (d.getPoint() < worst.getPoint()) worst = d;
        return worst;
    }
    /**
     * The Bot choose an action to do during his turn
     *
     * @param remainingActions Set of actions that the bot could do during this turn
     * @return The action choose by the bot
     */
    @Override
    public Action nextAction(Set<Action> remainingActions) {
        var objective = districtObjective();
        if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
            return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
        if (remainingActions.contains(Action.DRAW))
            return Action.DRAW;// Draw districts in the deck if the bot has no more cards in hand
        if (remainingActions.contains(Action.BUILD) && objective.isPresent() && objective.get().getCost() <= getCoins())
            return Action.BUILD;// Build a district if the bot has an objective and if it has enough money to build the objective
        if (remainingActions.contains(Action.SPECIAL_INCOME) && quantityOfColorBuilt(getCharacter().orElseThrow().getColor()) > 0)
            return Action.SPECIAL_INCOME;// Pick coins according to the built districts if the ability of the chosen character allows it
        if (remainingActions.contains(Action.DISCARD) && getHandDistricts().size() > 1 && (objective.isPresent() && objective.get().getCost() > getCoins())) 
            return Action.DISCARD;// Discard a card to receive one coin if there are at least two cards in hand and need money to build the objective
        if (remainingActions.contains(Action.TAKE_THREE) && getCoins() > 3 && getHandDistricts().isEmpty() && objective.isEmpty())
            return Action.TAKE_THREE;// Take three cards and pay 3 coins if it has enough money, no objective and it needs cards.
        if (remainingActions.contains(Action.STEAL))
            return Action.STEAL;// Try to steal a character if the player's character is the Thief
        if (remainingActions.contains(Action.KILL))
            return Action.KILL;// Try to kill a character if the player's character is the Assassin
        if (remainingActions.contains(Action.EXCHANGE_PLAYER) && choosePlayerToExchangeCards(getGameStatus().getCardsNumber()) != null)
            return Action.EXCHANGE_PLAYER;
        if (remainingActions.contains(Action.EXCHANGE_DECK) && !chooseCardsToExchangeWithDeck().isEmpty())
            return Action.EXCHANGE_DECK;
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
    public Optional<Colors> pickBonusColor(Set<Colors> tookColors) {
        for (Colors color : Colors.values()) {
            if (color != Colors.NONE && !tookColors.contains(color)) {
                return Optional.of(color);
            }
        }
        return Optional.empty();
    }

    @Override
    public Character chooseCharacterToRob(List<Character> characterList) {
        return characterList.get(0); //TODO : this implementation is too basic, it must be updated
    }

    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        return characterList.get(0); //TODO : this implementation is too basic, it must be updated
    }

    /**
     * The bot chooses a player to exchange cards with. He chooses if the other has more cards than him, else, he doesn't do the action
     *
     * @param players List of player with whose he can exchange
     * @return The player chose for the exchange if there is an exchange
     */
    public String choosePlayerToExchangeCards(Map<String, Integer> players) {
        String playerToExchange = null;
        int nbCards = 0;
        int handSize = getHandDistricts().size();
        for (Map.Entry<String, Integer> p : players.entrySet()) {
            if (p.getKey().equals(this.getName())) continue;
            int playerHandSize = p.getValue();
            if (playerHandSize > handSize && playerHandSize > nbCards) {
                playerToExchange = p.getKey();
                nbCards = playerHandSize;
            }

        }
        return playerToExchange;
    }

    /**
     * The bot chooses a player to exchange cards with. He chooses if the other has more cards than him, else, he doesn't do the action
     *
     * @param players List of player with whose he can exchange
     * @return The player chose for the exchange if there is an exchange
     */
    @Override
    public Player playerToExchangeCards(List<Player> players) {
        String res = choosePlayerToExchangeCards(getGameStatus().getCardsNumber());
        assert (res != null);
        Player pChoose = null;
        for (Player p : players) {
            if (p.getName().equals(res)) pChoose = p;
        }
        assert (pChoose != null);
        return pChoose;
    }

    /**
     * The bot chooses a few cards to exchange with the deck. He chooses the card if its profitability is under 1.
     *
     * @return The List of cards he wants to exchange with the deck
     */
    public List<District> chooseCardsToExchangeWithDeck() {
        List<District> cardToExchange = new ArrayList<>();
        for (District d : getHandDistricts()) {
            if (districtProfitability(d) < 1) cardToExchange.add(d);
        }
        return cardToExchange;
    }
}
