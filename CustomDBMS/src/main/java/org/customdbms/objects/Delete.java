package org.customdbms.objects;

import org.customdbms.data.DataSource;
import org.customdbms.services.Transaction;

import java.util.*;

/**
 * Delete query class
 */
public class Delete extends Query {
    String tableName;
    HashMap<String, String> columnsAndValues;
    LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable;
    String whereClause;

    /**
     * Constructor for delete query
     *
     * @param queryString query string
     * @param buffer transaction buffer
     */
    public Delete(String queryString, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        columnsAndValues = new HashMap<>();
        setup(queryString);
        dataSource.setupDB();
        DataSource.buffer=buffer;
    }

    /**
     * execute logic for delete query
     *
     * @return execution status
     */
    @Override
    public boolean execute() {
        try {
            validate();
            parse();
            dataSource.buildTable();

            filterTable = dataSource.table;
            List<Integer> deleteRows = new ArrayList<>();

            if (whereClause != null) {
                String[] lhsrhs;
                String action;
                whereClause = whereClause.replaceAll(" ", "");
                whereClause = whereClause.replaceAll("'", "");

                if (whereClause.contains("=")) {
                    lhsrhs = whereClause.split("=");
                    action = "=";
                } else if (whereClause.contains("<")) {
                    lhsrhs = whereClause.split("<");
                    action = "<";
                } else if (whereClause.contains(">")) {
                    lhsrhs = whereClause.split(">");
                    action = ">";
                } else {
                    System.out.println("Invalid where clause");
                    return false;
                }

                for (Integer id : filterTable.keySet()) {
                    LinkedHashMap<String, String> row = filterTable.get(id);

                    String lhs = row.get(lhsrhs[0]).trim();
                    String rhs = lhsrhs[1].trim();
                    switch (action) {
                        case "=":
                            if (lhs.equals(rhs))
                                deleteRows.add(id);
                            break;
                        case "<":
                            if (Float.parseFloat(lhs) < Float.parseFloat(rhs))
                                deleteRows.add(id);
                            break;
                        case ">":
                            if (Float.parseFloat(lhs) > Float.parseFloat(rhs))
                                deleteRows.add(id);
                            break;
                    }
                }
            } else
                deleteRows = new ArrayList<>(filterTable.keySet());

            deleteRowsDataTable(filterTable, deleteRows);

            DataSource.appendExecutionMessage("Deleted successfully.");
            dataSource.updateFile(filterTable);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * delete rows from table
     *
     * @param filterTable intermediate table
     * @param deleteRows  rows to delete
     */
    private void deleteRowsDataTable(LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable, List<Integer> deleteRows) {
        for (Integer deleteRow : deleteRows) {
            filterTable.remove(deleteRow);
        }
    }

    /**
     * validates user input
     *
     * @return validation status
     */
    private boolean validate() {

        if (!queryString.contains("delete"))
            return false;

        else if (!queryString.contains("from"))
            return false;

        else if (!queryString.matches("^[a-zA-Z0-9(_) <>=']+$"))
            return false;

        else if (queryCharacterCount.get("(") != queryCharacterCount.get(")"))
            return false;

        return true;
    }

    /**
     * parsing logic for input query
     *
     * @return parsing status
     */
    private boolean parse() {
        try {
            String query = this.queryString;

            query = query.replace("delete from", "");
            int startIndex = 0;
            int endIndex = (query.contains("where")) ? query.indexOf("where") : query.length();

            tableName = query.substring(startIndex, endIndex).trim();

            if (!tableName.matches("^[a-zA-Z][a-zA-Z0-9-_]+$"))
                throw new Exception("parsing error");

            dataSource.setTableName(tableName);

            if (query.contains("where"))
                whereClause = query.substring(query.indexOf("where") + 5);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
