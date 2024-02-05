package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Robot player
 *
 * @author Team C
 */
public class Bot extends Player {
    /**
     * Level of security felt by the bot if his districts can't be destroyed
     */
    protected double nonDestructibleSecurity() {
        return 1;
    }
    /**
     * Level of security felt by the bot if he gets the crown
     */
    protected double crownSecurity() {
        return 2;
    } // Because the interest of the crown is to feel secured
    /**
     * Level of earnings in case there's a startup income
     */
    protected double startupIncomeCoin() {
        return 1;
    }  // There's an income, so the bot could get more coins
    /**
     * Level of the need to draw cards if there's a startup income
     */
    protected double startupIncomeCard() {
        return 0.5;
    }
    /**
     * Level of card gained if the bot could draw extra cards
     */
    protected double beginDrawCard() {
        return 2;
    }
    /**
     * Level of earnings if the bot could draw extra cards
     */
    protected double beginDrawCoin() {
        return 1;
    }
    /**
     * Level of earnings if the bot has the crown
     */
    protected double crownCoin() {
        return 0.125;
    }
    /**
     * Level of potential builds if the bot has the crown
     */
    protected double crownBuild() {
        return 0.125;
    }
    /**
     * Level of card gained if the bot has the crown
     */
    protected double crownCard() {
        return 0.125;
    }
    /**
     * Level of the need to attack other players if the bot has the crown
     */
    protected double crownFear() {
        return 0.125; // If the bot takes the crown, he is pretty neutral
    }
    /**
     * Level of security the bot feels if he could kill another player
     */
    protected double killSecurity() {
        return 0.75;
    }
    /**
     * Level of the need to target other players the bot feels if he could kill another player
     */
    protected double killFear() {
        return 1;
    }
    /**
     * Level of security the bot feels if he could steal another player
     */
    protected double stealSecurity() {
        return 0.25;
    }
    /**
     * Level of the need to target other players the bot feels if he could steal another player
     */
    protected double stealFear() {
        return 0.5;
    }
    /**
     * Level of potential earnings of the bot if he could steal another player
     */
    protected double stealCoin() {
        return 1;
    }
    /**
     * Level of card gained if the player could exchange cards with the deck
     */
    protected double exchangeDeckCard() {
        return 1;
    }
    /**
     * Level of card gained if the bot could exchange cards with another player
     */
    protected double exchangePlayerCard() {
        return 1;
    }
    /**
     * Level of the need to target another player if the bot could exchange cards with another player
     */
    protected double exchangePlayerFear() {
        return 0.5;
    }
    /**
     * Level of the need to attack another player if the bot could destroy another player's district
     */
    protected double destroyFear() {
        return 2;
    }
    /**
     * Impact level of the number of district of the same color
     */
    protected double coloredDistrictMultiplier() {
        return 1;
    }
    /**
     * Impact level of the number of district left to build
     */
    protected double numberOfDistrictToBuildMultiplier() {
        return 1;
    }
    private HashMap<IPlayer, List<Character>> possibleCharacters;

    public Bot(String name) {
        super(name, 0, Collections.emptyList());
    }

    public Bot(String name, int coins, List<District> districts) {
        super(name, coins, districts);
    }


    /**
     * Add the possible characters for players
     *
     * @param players    the players to be added
     * @param characters the available characters for the players
     */
    private void addPossibleCharacters(List<IPlayer> players, List<Character> characters) {
        for (IPlayer player : players) {
            possibleCharacters.put(player, characters);
        }
    }

    /**
     * Initialize the HashMap with possible characters for each player
     *
     * @param availableCharacters the available characters
     * @param beforePlayers       the player who have chosen before
     */
    public void setPossibleCharacters(List<Character> availableCharacters, List<IPlayer> beforePlayers) {
        possibleCharacters = new HashMap<>();

        // Obtaining the chosen characters before the bot
        List<Character> beforeCharacters = new ArrayList<>(Game.defaultCharacterList());
        beforeCharacters.removeAll(availableCharacters);

        // Obtaining characters that was available to the bot without its choice
        List<Character> afterCharacters = new ArrayList<>(availableCharacters);
        afterCharacters.remove(getCharacter().orElseThrow());

        // Obtaining the players who will choose after the bot
        List<IPlayer> afterPlayers = new ArrayList<>(getPlayers());
        afterPlayers.removeAll(beforePlayers);
        afterPlayers.remove(this);

        // Addition of all the obtained data in possibleCharacters
        addPossibleCharacters(beforePlayers, beforeCharacters);
        addPossibleCharacters(afterPlayers, afterCharacters);
    }

    @Override
    public Character pickCharacter(List<Character> availableCharacters) {
        super.pickCharacter(availableCharacters);
        Character best = null;
        double maxProfitability = -100;
        for (Character character : availableCharacters) {
            var profitability = characterProfitability(character);
            if (profitability <= maxProfitability) continue;
            best = character;
            maxProfitability = profitability;
        }
        assert best != null;
        setCharacter(best);
        return best;
    }

    /**
     * Calculate a profitability score for a given character
     *
     * @param character The character whose profitability is to be calculated
     */
    protected double characterProfitability(Character character) {
        if (getPlayers() == null) return 0;

        List<SimpleEntry<District, Double>> districtsByProfitability = getHandDistricts().stream()
                .map(district -> new SimpleEntry<>(district, districtProfitability(district)))
                .sorted(Comparator.<SimpleEntry<District, Double>>comparingDouble(SimpleEntry::getValue).reversed()).toList();
        int availableCoins = getCoins();
        double coinNecessity = (Game.DISTRICT_NUMBER_TO_END - getBuiltDistricts().size() - districtsByProfitability.stream().takeWhile(district -> availableCoins - district.getKey().getCost() > 0).count()) / (double) Game.DISTRICT_NUMBER_TO_END;
        double securityNecessity = getBuiltDistricts().size() / (double) Game.DISTRICT_NUMBER_TO_END;
        double buildNecessity = (1 - coinNecessity) * getBuiltDistricts().size() / Game.DISTRICT_NUMBER_TO_END;
        double cardNecessity = 1.0 / (getHandDistricts().size() + 1); // The need to gain cards
        double fear = 0.5 + getPlayers().stream().mapToInt(built -> built.getBuiltDistricts().size() - getBuiltDistricts().size()).max().orElse(0) / 16.0; // The need to handicap other players

        double coinProfitability = quantityOfColorBuilt(character.getColor()) * coloredDistrictMultiplier();
        double securityProfitability = 0;
        double buildProfitability = character.numberOfDistrictToBuild() * numberOfDistrictToBuildMultiplier();
        double cardProfitability = 0;
        double fearProfitability = 0;

        if (!character.canHaveADistrictDestroyed()) securityProfitability += nonDestructibleSecurity();
        switch (character.startTurnAction()) {
            case STARTUP_INCOME -> {
                coinProfitability += startupIncomeCoin();
                cardProfitability += startupIncomeCard(); // Because if there's already an income there's less need to use the income action
            }
            case BEGIN_DRAW -> {
                cardProfitability += beginDrawCard();
                coinProfitability += beginDrawCoin();
            }
            case GET_CROWN -> {
                coinProfitability += crownCoin();
                securityProfitability += crownSecurity();
                buildProfitability += crownBuild();
                cardProfitability += crownCard();
                fearProfitability += crownFear();
            }
            default -> { /* do nothing */ }
        }
        for (Action action : character.getAction()) {
            switch (action) {
                case KILL -> {
                    securityProfitability += killSecurity();
                    fearProfitability += killFear();
                }
                case STEAL -> {
                    securityProfitability += stealSecurity();
                    fearProfitability += stealFear();
                    coinProfitability += getPlayers().stream()
                            .filter(entry -> !Objects.equals(entry.getName(), getName())).mapToInt(IPlayer::getCoins).average().orElse(0)
                            * stealCoin();
                }
                case EXCHANGE_DECK ->
                        cardProfitability += districtsByProfitability.stream().filter(entry -> entry.getValue() < 1).count() * exchangeDeckCard();
                case EXCHANGE_PLAYER -> {
                    IPlayer playerToExchangeWith = playerToExchangeCards(getPlayers());
                    if (playerToExchangeWith == null) break;
                    cardProfitability += playerToExchangeWith.getHandSize() - getHandDistricts().size() * exchangePlayerCard();
                    fearProfitability += exchangePlayerFear();
                }
                case DESTROY -> fearProfitability += destroyFear();
                default -> { /* do nothing */ }
            }
        }

        return coinProfitability * coinNecessity
                + securityProfitability * securityNecessity
                + buildProfitability * buildNecessity
                + cardProfitability * cardNecessity
                + fearProfitability * fear;
    }

    /**
     * Calculates the gain on a property from the construction of a district compared to the current one
     *
     * @param district         The district in question
     * @param districtProperty Getter of the property on the district side
     * @param playerProperty   Getter of the property on the player side
     * @return The difference between the two
     */
    protected double districtPropertyGain(District district, Function<District, Optional<? extends Number>> districtProperty, DoubleSupplier playerProperty) {
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
        if (getBuiltDistricts().contains(district)) return -1; // We can't build the same district twice
        return district.getPoint()
                + quantityOfColorBuilt(district.getColor()) / (double) Game.DISTRICT_NUMBER_TO_END
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
            if (bestDistrict == null || profitability > bestProfitability || (profitability == bestProfitability && district.getCost() < bestDistrict.getCost()) && districtProfitability(district) >= 0) {
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
        var wantsToExchangeWithPlayer = remainingActions.contains(Action.EXCHANGE_PLAYER) && playerToExchangeCards(getPlayers()) != null;

        if (remainingActions.contains(Action.INCOME) && ((objective.isPresent() && objective.get().getCost() > getCoins()) || getHandDistricts().size() >= 4))
            return Action.INCOME;// Pick coins if the bot has an objective and the objective cost more than what he has or if the bot already has a lot of cards in hand
        if (remainingActions.contains(Action.DRAW) && !wantsToExchangeWithPlayer)
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
        if (wantsToExchangeWithPlayer)
            return Action.EXCHANGE_PLAYER;
        if (remainingActions.contains(Action.EXCHANGE_DECK) && !chooseCardsToExchangeWithDeck().isEmpty())
            return Action.EXCHANGE_DECK;
        if (remainingActions.contains(Action.DESTROY) && destroyDistrict(getPlayers()) != null)
            return Action.DESTROY;// The player wants to destroy a district
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

    /**
     * Gives the most constructed color with its number of occurrences
     *
     * @param player the player we want to inspect
     * @return a tuple with the most constructed color and its number of occurrences
     */
    private Map.Entry<Colors, Long> mostConstructedColor(IPlayer player) {
        return player.getBuiltDistricts().stream()
                .collect(Collectors.groupingBy(District::getColor, Collectors.counting()))
                .entrySet().stream().max(Comparator.comparingLong(Map.Entry::getValue)).orElse(null);
    }

    /**
     * Estimates the character of a player
     *
     * @param player              the player we want to inspect
     * @param availableCharacters the characters that can be chosen
     * @return the estimated character of a player
     */
    private Character characterEstimation(IPlayer player, List<Character> availableCharacters) {
        List<Character> characters = possibleCharacters.getOrDefault(player, availableCharacters);
        characters.retainAll(availableCharacters);
        if (characters.size() == 1) return characters.get(0);
        Character maxCharacter = moreProbableCharacterForPlayer(characters, player);
        return (maxCharacter != null) ? maxCharacter : availableCharacters.get(0);
    }

    /**
     * Returns the more probable character by factors
     *
     * @param possiblesCharacters the possibles character for the player
     * @param player              the player to be estimated
     * @return the estimated character
     */
    private Character moreProbableCharacterForPlayer(List<Character> possiblesCharacters, IPlayer player) {
        Map.Entry<Colors, Long> maxColor = mostConstructedColor(player);
        int playerCoins = player.getCoins();
        double maxFactor = 0;
        Character maxCharacter = null;
        for (Character character : possiblesCharacters) {
            double characterProbability = characterProbabilityForPlayer(player, character, maxColor, playerCoins);
            if (characterProbability > maxFactor) {
                maxFactor = characterProbability;
                maxCharacter = character;
            }
        }
        return maxCharacter;
    }

    /**
     * Gives the character probability for the player
     *
     * @param player      the analyzed player
     * @param character   the character of which we want the probability
     * @param maxColor    the most constructed color and its number of occurrences
     * @param playerCoins the player coins
     * @return the probability that this player has to have this character
     */
    private double characterProbabilityForPlayer(IPlayer player, Character character, Map.Entry<Colors, Long> maxColor, int playerCoins) {
        double probability = 1.0 / possibleCharacters.size();
        if (maxColor != null && maxColor.getKey() == character.getColor())
            probability += Math.min((maxColor.getValue() / 2), 1) * 0.3;
        // if the player has a lot of coins and the current character can build more districts
        if (character.numberOfDistrictToBuild() > 1)
            probability += Math.min((playerCoins / 6.0), 1) * 0.2;
        // if the player has a small hand size and the current character can obtain more districts
        if (character.startTurnAction().equals(Action.BEGIN_DRAW) && player.getHandSize() > 0)
            probability += Math.min((1 / player.getHandSize()), 1) * 0.2;
        // if the player has a little number of coins and the current character gives more coins
        if (character.startTurnAction().equals(Action.STARTUP_INCOME))
            probability += (playerCoins > 0) ? Math.min((2 / playerCoins), 1) * 0.15 : 0.15;
        return probability;
    }

    /**
     * @return the average of built districts per player
     */
    private double builtDistrictsAverage() {
        return getPlayers().stream()
                .map(IPlayer::getBuiltDistricts)
                .mapToInt(List::size)
                .average().orElse(0f);
    }

    /**
     * @param attributeExtractor the attribute extractor
     * @return the player with the max attribute and the number associated
     */
    private SimpleEntry<IPlayer, Integer> playerWithMaxAttribute(ToIntFunction<IPlayer> attributeExtractor) {
        return getPlayers().stream()
                .max(Comparator.comparingInt(attributeExtractor))
                .map(player -> new SimpleEntry<>(player, attributeExtractor.applyAsInt(player)))
                .orElseThrow();
    }

    /**
     * @param characterList the list of character the player can rob
     * @return the character to rob
     */
    @Override
    public Character chooseCharacterToRob(List<Character> characterList) {
        return characterEstimation(playerWithMaxAttribute(IPlayer::getCoins).getKey(), characterList);
    }

    /**
     * @param characterList the list of character the player can kill
     * @return the character to kill
     */
    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        SimpleEntry<IPlayer, Integer> maxBuilder = playerWithMaxAttribute(player -> player.getBuiltDistricts().size());
        SimpleEntry<IPlayer, Integer> richestPlayer = playerWithMaxAttribute(IPlayer::getCoins);
        IPlayer bestPlayerToChoose;
        int maxBuilderSize = possibleCharacters.get(maxBuilder.getKey()).size();
        int richestPlayerSize = possibleCharacters.get(richestPlayer.getKey()).size();

        if (maxBuilderSize == richestPlayerSize)
            bestPlayerToChoose = (maxBuilder.getValue() - builtDistrictsAverage() > 2) ? maxBuilder.getKey() : richestPlayer.getKey();
        else
            bestPlayerToChoose = (maxBuilderSize > richestPlayerSize) ? richestPlayer.getKey() : maxBuilder.getKey();
        return characterEstimation(bestPlayerToChoose, characterList);
    }

    /**
     * The bot chooses a player to exchange cards with. He chooses if the other has more cards than him, else, he doesn't do the action
     *
     * @param players List of player with whose he can exchange
     * @return The player chose for the exchange if there is an exchange
     */
    @Override
    public IPlayer playerToExchangeCards(List<IPlayer> players) {
        IPlayer playerToExchange = null;
        int nbCards = 0;
        int handSize = getHandDistricts().size();
        for (IPlayer p : players) {
            int playerHandSize = p.getHandSize();
            if (playerHandSize > handSize && playerHandSize > nbCards) {
                playerToExchange = p;
                nbCards = playerHandSize;
            }

        }
        return playerToExchange;
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

    /**
     * Get a list of the players names sorted by their dangerousness level
     * (number of district built, then amount of purple district built)
     */
    protected List<IPlayer> getMostDangerousPlayersByBuiltDistricts(List<IPlayer> districtBuilt) {
        return districtBuilt.stream()
                .filter(player -> !player.equals(this))
                .sorted(Comparator.comparing((IPlayer player) -> player.getBuiltDistricts().size())
                        .thenComparingLong((IPlayer player) -> player.getBuiltDistricts().stream()
                                .filter(district -> district.getColor() == Colors.PURPLE).count()).reversed()).toList();
    }

    /**
     * The bot chooses a district to destroy among the districts
     *
     * @param players List of players whose districts can be destroyed
     * @return The district to destroy
     */
    @Override
    public SimpleEntry<IPlayer, District> destroyDistrict(List<IPlayer> players) {
        List<IPlayer> playerToTargetList = getMostDangerousPlayersByBuiltDistricts(players);
        return players.stream()
                .filter(player -> !player.equals(this))
                .flatMap(entry -> entry.getDestroyableDistricts().stream().map(v -> new SimpleEntry<>(entry, v)))
                .filter(entry -> entry.getValue().isDestructible() && (entry.getValue().getCost() - 1 <= getCoins() - 1) || entry.getValue().getCost() == 1)
                .max(Comparator.<SimpleEntry<IPlayer, District>>comparingInt(entry -> playerToTargetList.indexOf(entry.getKey())).reversed()
                        .thenComparingInt(entry -> entry.getValue().getColor() == Colors.PURPLE ? 1 : 0)
                        .thenComparingInt(entry -> entry.getValue().getPoint())).orElse(null);
        /* We order the district list first on the purple colour, then on the district's points.
        We remove the district that the bot can't destroy, and we remove a district if its destruction costs all the bots coins */
    }

    /**
     * Does the player want to recover a district that has just been destroyed
     *
     * @param district the destroyed district
     * @return true if the bot wants to take the card
     */
    public boolean wantsToTakeADestroyedDistrict(District district) {
        double handAverageProfitability = getHandDistricts().stream().map(this::districtProfitability).mapToDouble(Double::doubleValue).average().orElse(0);
        return districtProfitability(district) >= handAverageProfitability && getCoins() > 1; // The bot takes the destroyed district if he has more than 1 coin after paying the district and the district profitability is above average of his hand
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
