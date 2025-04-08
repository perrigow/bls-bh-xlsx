package net.blackcrate.bls_bh_xlsx;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BowlerRecord {

    private int totalGames;
    private int totalPins;

    public final String name;
    public final List<BowlerStatistic> gamesStats = new LinkedList<>(
        Arrays.asList(
            new BowlerStatistic("<100", 99),
            new BowlerStatistic("100's", 124),
            new BowlerStatistic("125's", 149),
            new BowlerStatistic("150's", 174),
            new BowlerStatistic("175's", 199),
            new BowlerStatistic("200's", 224),
            new BowlerStatistic("225's", 249),
            new BowlerStatistic("250's", 274),
            new BowlerStatistic("275's", 299),
            new BowlerStatistic("300's", 300)
    ));
    public final List<BowlerStatistic> seriesStats = new LinkedList<>(
        Arrays.asList(
            new BowlerStatistic("<400", 399),
            new BowlerStatistic("400's", 449),
            new BowlerStatistic("450's", 499),
            new BowlerStatistic("500's", 549),
            new BowlerStatistic("550's", 599),
            new BowlerStatistic("600's", 649),
            new BowlerStatistic("650's", 699),
            new BowlerStatistic("700's", 749),
            new BowlerStatistic("750's", 799),
            new BowlerStatistic("800's", 900)
    ));

    public BowlerRecord(String name) {
        this.name = name;
        this.totalGames = 0;
        this.totalPins = 0;
    }

    private static void updateStats(List<BowlerStatistic> stats, int value) {
        for (BowlerStatistic stat : stats) {
            if (value <= stat.limit) {
                stat.addOne();
                break;
            }
        }
    }

    public void addGame(int game) {
        updateStats(gamesStats, game);
        this.totalPins += game;
        this.totalGames++;
    }

    public void addSeries(int series) {
        updateStats(seriesStats, series);
    }

    public float avg () {
        return (float) this.totalPins / this.totalGames;
    }

    @Override
    public String toString() {
        StringBuilder header = new StringBuilder();
        StringBuilder values = new StringBuilder();
        header.append(String.format("%-20s", "Name"));
        values.append(String.format("%-20s", this.name));
        for (BowlerStatistic stat : this.gamesStats) {
            header.append(String.format("%-7s", stat.category));
            values.append(String.format("%-7d", stat.count()));
        }
        for (BowlerStatistic stat : this.seriesStats) {
            header.append(String.format("%-7s", stat.category));
            values.append(String.format("%-7d", stat.count()));
        }
        header.append(String.format("%-10s", "Final Avg"));
        values.append(String.format("%-10.2f", this.avg()));

        return header.toString() + "\n" + values.toString();
    }

}
