package org.customdbms.objects;

import org.customdbms.common.Constants;
import org.customdbms.data.DataSource;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * class for use query implementation
 */
public class Use extends Query {
    public String queryString;

    /**
     * constructor for use query
     *
     * @param query  query string
     * @param buffer transaction buffer
     */
    public Use(String query, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        this.queryString = query;
        setup(query);
        DataSource.buffer=buffer;
    }

    /**
     * execution logic
     *
     * @return execution status
     */
    @Override
    public boolean execute() {
        String dbName = queryString.trim().replace(";", "").substring(queryString.indexOf("use ") + 4);
        if(dataSource.setDBName(dbName)) {
            System.out.println(Constants.MSG_DEFAULTDATABASE);
            return true;
        }
        else
            return false;
    }
}
