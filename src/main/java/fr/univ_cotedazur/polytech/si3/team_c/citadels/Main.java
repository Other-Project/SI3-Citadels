package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    private static final Logger LOGGER = Logger.getGlobal();
    @Parameter(names = "--2thousands")
    private static boolean twoThousand = false;

    @Parameter(names = "--demo")
    private static boolean demo = false;


    public static void main(String... args) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s%6$s%n");
        if (demo) {
            Arrays.stream(LOGGER.getParent().getHandlers()).forEach(handler -> handler.setLevel(Level.FINEST));
            LOGGER.setLevel(Level.FINEST);
        }
        if (twoThousand) twoThousandGames();
        else {
            HashMap<String, Statistic> res = playMultipleGames(1, 4);
            LOGGER.info(res::toString);
        }
    }

    private static HashMap<String, Statistic> playMultipleGames(int numberOfGames, int numberOfPlayers, Player... players) {
        HashMap<String, Statistic> stat = new HashMap<>() {
            @Override
            public String toString() {
                StringBuilder message = new StringBuilder("Statistic measures on ").append(numberOfGames).append(" games :\n");
                for (Map.Entry<String, Statistic> entry : this.entrySet())
                    message.append(entry.getKey()).append("\n\t").append(this.get(entry.getKey()).toString()).append("\n");
                return message.toString();
            }
        };

        for (int k = 0; k < numberOfGames; k++) {
            Game game = new Game(numberOfPlayers, players);
            game.getPlayerList().forEach(player -> stat.putIfAbsent(player.getName(), new Statistic()));
            game.start();

            SimpleEntry<List<Player>, Integer> winners = game.getWinners();
            List<String> botWinners = winners.getKey().stream().map(Player::getName).toList();

            if (botWinners.size() == 1) stat.get(botWinners.get(0)).addWin();
            else botWinners.forEach(bot -> stat.get(bot).addEquality());

            List<String> loosers = new ArrayList<>(game.getPlayerList().stream().map(Player::getName).toList());
            loosers.removeAll(botWinners.stream().toList());
            loosers.forEach(bot -> stat.get(bot).addLoose());
        }
        return stat;
    }

    public static void twoThousandGames() {
        HashMap<String, Statistic> res = playMultipleGames(1000, 4, new Bot("Bot"), new DiscreetBot("Discrete Bot"), new FearFulBot("Fearful Bot"), new AgressiveBot("Aggressive Bot"));
        LOGGER.info(res::toString);
        res = playMultipleGames(1000, 4, new Bot("Bot 1"), new Bot("Bot 2"), new Bot("Bot 3"), new Bot("Bot 4"));
        LOGGER.info(res::toString);
    }
}
