package be.home.mezzmo.domain.dao.jdbc;

/**
 * Created by Gebruiker on 19/12/2016.
 */
public enum SQLFunction {
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
