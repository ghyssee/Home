package be.home.common.database.sqlbuilder;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class LimitBy implements Serializable {
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
