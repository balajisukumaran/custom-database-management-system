package org.customdbms.objects;

import org.customdbms.data.DataSource;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Rollback query class
 */
public class Rollback extends Query {

    /**
     * constructor for rollback query class
     *
     * @param query  rollback query
     * @param buffer transaction buffer
     */
    public Rollback(String query, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        super();
        setup(query);
        dataSource.setupDB();
        DataSource.buffer=buffer;
    }

    /**
     * execute method for rollback
     *
     * @return execution status
     */
    @Override
    public boolean execute() {
        System.out.println();
        return DataSource.clearBuffer();
    }
}
