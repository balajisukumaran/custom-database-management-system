package org.customdbms.objects;

import org.customdbms.common.Constants;
import org.customdbms.data.DataSource;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * class for use create database implementation
 */
public class CreateDatabase extends Query {

    String queryString;

    /**
     * constructor for create database query
     *
     * @param query  query string
     * @param buffer transaction buffer
     */
    public CreateDatabase(String query, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        super();
        setup(query);
        this.queryString = query;
        DataSource.buffer=buffer;
    }

    /**
     * execution logic
     *
     * @return execution status
     */
    @Override
    public boolean execute() {
        String dbName = queryString.trim().replace(";", "").substring(queryString.indexOf("create database ") + 16);

        if(dataSource.setDBName(dbName)) {
            System.out.println(Constants.MSG_DATABASE);
            return true;
        }

        return false;
    }
}
