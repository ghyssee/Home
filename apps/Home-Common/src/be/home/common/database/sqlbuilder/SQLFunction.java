package be.home.common.database.sqlbuilder;

import java.io.Serializable;

/**
 * Created by Gebruiker on 19/12/2016.
 */
public enum SQLFunction implements Serializable {
    UPPER (0),
    MAX(0),
    NONE(0);

    public int parameters;

    SQLFunction(int p) {
        this.parameters = p;
    }

    public int getParameters() {
        return parameters;
    }

    }
