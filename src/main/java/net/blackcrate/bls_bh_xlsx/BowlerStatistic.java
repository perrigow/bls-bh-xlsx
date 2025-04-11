package net.blackcrate.bls_bh_xlsx;

public class BowlerStatistic {

    public final String category;
    public final int limit;
    private int count;

    public BowlerStatistic(String category, int limit) {
        this.category = category;
        this.limit = limit;
        count = 0;
    }

    public void addOne() {
        count++;
    }

    public int count() {
        return count;
    }

    @Override
    public String toString() {
        return String.format(
            "category=%s, limit=%d, count=%d",
            category, limit, count
        );
    }

}
