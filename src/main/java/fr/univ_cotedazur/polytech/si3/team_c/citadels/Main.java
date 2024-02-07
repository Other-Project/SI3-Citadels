package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import fr.univ_cotedazur.polytech.si3.team_c.citadels.players.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s%6$s%n");
    }


    public static final Path STATISTICS_PATH = Paths.get("stats", "gamestats.csv");

    private static final Logger LOGGER = Logger.getGlobal();
    @Parameter(names = "--2thousands")
    private boolean twoThousand = false;

    @Parameter(names = "--demo")
    private boolean demo = false;

    @Parameter(names = "--csv")
    private boolean csv = false;


    public static void main(String... args) throws Exception {
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);
        if (main.demo) {
            Arrays.stream(LOGGER.getParent().getHandlers()).forEach(handler -> handler.setLevel(Level.FINEST));
            LOGGER.setLevel(Level.FINEST);
        }
        List<Statistic> stats = main.csv ? loadCsv(STATISTICS_PATH) : new ArrayList<>();

        List<Statistic> results;
        if (main.twoThousand) {
            results = playMultipleGames(1000, 5, new Bot("Bot"), new DiscreetBot("Discrete Bot"), new FearFulBot("Fearful Bot"), new AgressiveBot("Aggressive Bot"), new RandomBot("Random Bot"));
            LOGGER.info(results::toString);
            stats.addAll(results);
            results = playMultipleGames(1000, 4, new Bot("Bot 1"), new Bot("Bot 2"), new Bot("Bot 3"), new Bot("Bot 4"));
        } else results = playMultipleGames(1, 4);
        LOGGER.info(results::toString);
        stats.addAll(results);

        if (main.csv) writeCsv(STATISTICS_PATH, stats);
    }

    private static List<Statistic> playMultipleGames(int numberOfGames, int numberOfPlayers, Player... players) {
        HashMap<String, Statistic> stat = new HashMap<>();

        for (int k = 0; k < numberOfGames; k++) {
            Game game = new Game(numberOfPlayers, players);
            game.getPlayerList().forEach(player -> stat.putIfAbsent(player.getName(), new Statistic(player.getName())));
            game.start();

            SimpleEntry<List<Player>, Integer> winners = game.getWinners();
            List<String> botWinners = winners.getKey().stream().map(Player::getName).toList();

            if (botWinners.size() == 1) stat.get(botWinners.get(0)).addWin(winners.getValue());
            else botWinners.forEach(bot -> stat.get(bot).addEquality(winners.getValue()));

            List<Player> losers = new ArrayList<>(game.getPlayerList());
            losers.removeAll(winners.getKey());
            losers.forEach(bot -> stat.get(bot.getName()).addLoss(bot.getScore(game.getCurrentTurn())));
        }

        return new ArrayList<>(stat.values()) {
            @Override
            public String toString() {
                StringBuilder message = new StringBuilder("Statistic measures on ").append(numberOfGames).append(" game(s) :\n");
                for (Statistic entry : this)
                    message.append(entry.getName()).append("\n\t").append(entry).append("\n");
                return message.toString();
            }
        };
    }

    /**
     * Loads statistics data from a CSV file
     *
     * @param path Path of the file to read
     */
    public static List<Statistic> loadCsv(Path path) throws IOException {
        if (!Files.exists(path)) return new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path)) {
            CsvToBean<Statistic> cb = new CsvToBeanBuilder<Statistic>(reader)
                    .withType(Statistic.class)
                    .withSeparator(';')
                    .build();
            return cb.parse();
        }
    }

    /**
     * Writes statistics data in a CSV file
     *
     * @param path Path of the file to write in
     * @param data Data to write
     */
    public static void writeCsv(Path path, List<Statistic> data) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        Files.createDirectories(path.getParent());

        try (Writer writer = new FileWriter(path.toString())) {
            HeaderColumnNameMappingStrategy<Statistic> strategy = new HeaderColumnNameMappingStrategyBuilder<Statistic>().build();
            strategy.setType(Statistic.class);
            var columns = Arrays.stream(Statistic.class.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(CsvPosition.class))
                    .sorted(Comparator.comparingInt(field -> field.getAnnotation(CsvPosition.class).position()))
                    .map(field -> (field.isAnnotationPresent(CsvBindByName.class) ? field.getAnnotation(CsvBindByName.class).column() : field.getName()).toUpperCase())
                    .toArray();
            strategy.setColumnOrderOnWrite(Comparator.comparingInt(c -> ArrayUtils.indexOf(columns, c)));

            StatefulBeanToCsv<Statistic> sbc = new StatefulBeanToCsvBuilder<Statistic>(writer)
                    .withMappingStrategy(strategy)
                    .withSeparator(';')
                    .build();
            sbc.write(data);
        }
    }
}
