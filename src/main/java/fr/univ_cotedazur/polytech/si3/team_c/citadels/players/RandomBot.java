package fr.univ_cotedazur.polytech.si3.team_c.citadels.players;

import fr.univ_cotedazur.polytech.si3.team_c.citadels.Character;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.*;

import java.util.*;

public class RandomBot extends Player {
    private final Random random;

    public RandomBot(String name, int coins, List<District> districts) {
        this(name, coins, districts, new Random());
    }

    protected RandomBot(String name, int coins, List<District> districts, Random random) {
        super(name, coins, districts);
        this.random = random;
    }

    public RandomBot(String name) {
        this(name, 0, Collections.emptyList());
    }

    public <T> T randomElementFromList(List<T> list) {
        return (list.isEmpty()) ? null : list.get(random.nextInt(list.size()));
    }

    @Override
    public District cardToDiscard() {
        return randomElementFromList(getHandDistricts());
    }

    @Override
    public Character pickCharacter(CharacterManager characterManager) {
        super.pickCharacter(characterManager);
        Character chosenCharacter = randomElementFromList(characterManager.possibleCharactersToChoose());
        setCharacter(chosenCharacter);
        return chosenCharacter;
    }

    @Override
    public AbstractMap.SimpleEntry<IPlayer, District> destroyDistrict(List<IPlayer> players) {
        List<IPlayer> destroyablePlayers = players.stream()
                .filter(player -> !player.equals(this) && !player.getDestroyableDistricts().isEmpty())
                .toList();

        if (destroyablePlayers.isEmpty()) return null;

        IPlayer randomPlayer = destroyablePlayers.stream().skip(random.nextInt(destroyablePlayers.size())).findFirst().orElseThrow();
        return new AbstractMap.SimpleEntry<>(randomPlayer, randomElementFromList(randomPlayer.getDestroyableDistricts()));
    }

    @Override
    public Action nextAction(Set<Action> remainingActions) {
        List<Action> possibleActions = new ArrayList<>(remainingActions);
        if (remainingActions.contains(Action.SPECIAL_INCOME) && quantityOfColorBuilt(getCharacter().orElseThrow().getColor()) == 0)
            possibleActions.remove(Action.SPECIAL_INCOME);
        if (remainingActions.contains(Action.DISCARD) && getHandDistricts().size() <= 1)
            possibleActions.remove(Action.DISCARD);
        if (remainingActions.contains(Action.TAKE_THREE) && getCoins() < 3)
            possibleActions.remove(Action.TAKE_THREE);
        if (remainingActions.contains(Action.EXCHANGE_DECK) && !wantToExchangeCard())
            possibleActions.remove(Action.EXCHANGE_DECK);
        if (remainingActions.contains(Action.DESTROY) && destroyDistrict(getPlayers()) == null)
            possibleActions.remove(Action.DESTROY);
        return (possibleActions.isEmpty()) ? Action.NONE : randomElementFromList(possibleActions);
    }

    @Override
    public List<District> pickDistrictsFromDeck(List<District> drawnCards, int amountToChoose) {
        if (drawnCards.isEmpty()) return Collections.emptyList();
        List<District> pickedDistricts = new ArrayList<>(drawnCards);
        Collections.shuffle(pickedDistricts);
        pickedDistricts = pickedDistricts.subList(0, Math.min(amountToChoose, pickedDistricts.size()));
        pickedDistricts.forEach(this::addDistrictToHand);
        return pickedDistricts;
    }

    @Override
    protected List<District> pickDistrictsToBuild(int maxAmountToChoose, int turn) {
        List<District> districts = new ArrayList<>(getHandDistricts());
        List<District> toBuild = new ArrayList<>();
        Collections.shuffle(districts);
        Iterator<District> districtIterator = districts.iterator();
        while (districtIterator.hasNext() && maxAmountToChoose > 0) {
            District curDistrict = districtIterator.next();
            if (curDistrict.getCost() <= getCoins()) {
                toBuild.add(curDistrict);
                buildDistrict(curDistrict, turn);
                maxAmountToChoose--;
            }
        }
        return toBuild;
    }

    @Override
    public Character chooseCharacterToRob(List<Character> characterList) {
        return randomElementFromList(characterList);
    }

    @Override
    public Character chooseCharacterToKill(List<Character> characterList) {
        return randomElementFromList(characterList);
    }

    @Override
    public IPlayer playerToExchangeCards(List<IPlayer> playerList) {
        return randomElementFromList(playerList);
    }

    @Override
    public List<District> chooseCardsToExchangeWithDeck() {
        if (getHandSize() == 1) return new ArrayList<>(getHandDistricts());
        int numberToExchange = random.nextInt(1, getHandSize());
        List<District> shuffledDistricts = getHandDistricts();
        Collections.shuffle(shuffledDistricts);
        return shuffledDistricts.subList(0, numberToExchange);
    }

    public boolean wantToExchangeCard() {
        return !getHandDistricts().isEmpty() && random.nextInt(2) == 1;
    }

    @Override
    public boolean wantsToTakeADestroyedDistrict(District district) {
        return random.nextInt(2) == 1;
    }

    @Override
    public void setPossibleCharacters(List<IPlayer> beforePlayers, CharacterManager characterManager) {
        // Do nothing because the bot is random
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), random);
    }
}
