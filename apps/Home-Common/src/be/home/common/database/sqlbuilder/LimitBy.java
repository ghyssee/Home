package be.home.common.database.sqlbuilder;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class LimitBy {
    Integer pos;
    Integer total;

    LimitBy() {
        pos = null;
        total = null;
    }

    LimitBy(int pos) {
        this.pos = pos;
        total = null;
    }

    LimitBy(int pos, int total) {
        this.pos = pos;
        total = total;
    }
}
