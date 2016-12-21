package be.home.common.database.sqlbuilder;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class Option implements Serializable {
    String option;

    Option(String option) {
        this.option = option;
    }
}
