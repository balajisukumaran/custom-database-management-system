package org.customdbms.objects;

import org.customdbms.data.DataSource;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Begin Transaction object
 */
public class BeginTransaction extends Query {

    /**
     * Indicates that the flow is in transaction state
     *
     * @param query  input query
     * @param buffer transaction buffer
     */
    public BeginTransaction(String query, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        super();
        DataSource.buffer=buffer;
    }

    /**
     * flow is in transaction state
     *
     * @return true
     */
    @Override
    public boolean execute() {
        return true;
    }
}
