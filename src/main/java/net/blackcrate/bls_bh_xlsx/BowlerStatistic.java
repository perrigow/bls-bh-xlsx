package net.blackcrate.bls_bh_xlsx;

public class BowlerStatistic {

    public final String category;
    public final int limit;
    private int count;

    public BowlerStatistic(String category, int limit) {
        this.category = category;
        this.limit = limit;
        this.count = 0;
    }

    public void addOne() {
        this.count++;
    }

    public int count() {
        return this.count;
    }

    @Override
    public String toString() {
        return String.format(
            "category=%s, limit=%d, count=%d",
            this.category, this.limit, this.count
        );
    }

}
