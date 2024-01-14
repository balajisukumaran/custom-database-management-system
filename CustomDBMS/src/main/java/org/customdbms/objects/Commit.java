package org.customdbms.objects;

import org.customdbms.data.DataSource;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Commit query class
 */
public class Commit extends Query {

    /**
     * Constructor for commit query class
     *
     * @param query  user input query
     * @param buffer transaction buffer
     */
    public Commit(String query, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        setup(query);
        dataSource.setupDB();
        DataSource.buffer=buffer;
    }

    /**
     * executes the query
     *
     * @return t/f status of buffer commit
     */
    @Override
    public boolean execute() {
        return dataSource.commitBuffer();
    }
}
