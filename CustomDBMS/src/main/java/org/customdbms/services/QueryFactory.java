package org.customdbms.services;

import org.customdbms.objects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Query factory class
 */
public class QueryFactory {

    /**
     * returns appropriate query object
     *
     * @param queries input query
     * @return query object
     */
    public static ArrayList<Query> getQueryObject(ArrayList<String> queries, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        ArrayList<Query> queryObjects = new ArrayList<>();
        for (String query : queries) {
            if (query.trim().contains("start transaction")) queryObjects.add(new BeginTransaction(query, buffer));
            else if (query.trim().contains("create database")) queryObjects.add(new CreateDatabase(query, buffer));
            else if (query.trim().contains("use")) queryObjects.add(new Use(query, buffer));
            else if (query.trim().contains("create")) queryObjects.add(new Create(query, buffer));
            else if (query.trim().contains("insert")) queryObjects.add(new Insert(query, buffer));
            else if (query.trim().contains("select")) queryObjects.add(new Select(query, buffer));
            else if (query.trim().contains("update")) queryObjects.add(new Update(query, buffer));
            else if (query.trim().contains("delete")) queryObjects.add(new Delete(query, buffer));
            else if (query.trim().contains("commit")) queryObjects.add(new Commit(query, buffer));
            else if (query.trim().contains("rollback")) queryObjects.add(new Rollback(query, buffer));
            else queryObjects.add(null);
        }
        return queryObjects;
    }
}
