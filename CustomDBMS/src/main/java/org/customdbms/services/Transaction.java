package org.customdbms.services;

import org.customdbms.objects.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Singleton Transaction class
 */
public class Transaction {
    private static volatile Transaction instance;
    private static Object mutex = new Object();
    private static volatile HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer;

    /**
     * gets the singleton transaction object
     *
     * @return transaction object
     */
    public static Transaction getInstance() {
        Transaction result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Transaction();
            }
        }
        buffer = new HashMap<>();
        return result;
    }

    /**
     * executes the queries
     *
     * @param queries queries to execute
     * @return true in case of no error else false
     */
    public boolean executeQuries(ArrayList<String> queries) {

        if (!queries.get(queries.size() - 1).contains("commit")
                && !queries.get(queries.size() - 1).contains("rollback")
                && !queries.get(0).contains("create")
                && !queries.get(0).contains("use")) {
            queries.add("commit");
        }

        ArrayList<Query> queryObjects = QueryFactory.getQueryObject(queries, (queries.get(0).contains("transaction")) ? buffer : null);

        try {
            for (Query queryObject : queryObjects) {
                queryObject.execute();
                System.out.println("\n");
            }
        } catch (Exception e) {
            System.out.println("Transaction level error occurred.");
            return false;
        }

        return true;
    }


}