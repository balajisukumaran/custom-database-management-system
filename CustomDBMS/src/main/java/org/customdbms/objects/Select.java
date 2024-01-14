package org.customdbms.objects;

import org.customdbms.data.DataSource;

import java.util.*;

/**
 * select query class
 */
public class Select extends Query {
    HashMap<String, String> columnsAndValues;
    List<String> selectColumnList;
    LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable;
    String whereClause;

    /**
     * constructor for select query class
     *
     * @param queryString query string
     * @param buffer transaction buffer
     */
    public Select(String queryString, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
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
            List<Integer> addRows = new ArrayList<>();

            if (whereClause != null) {
                String[] lhsrhs;
                String action;
                whereClause = whereClause.replaceAll(" ", "");

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
                                addRows.add(id);
                            break;
                        case "<":
                            if (Float.parseFloat(lhs) < Float.parseFloat(rhs))
                                addRows.add(id);
                            break;
                        case ">":
                            if (Float.parseFloat(lhs) > Float.parseFloat(rhs))
                                addRows.add(id);
                            break;
                    }
                }
            } else
                addRows = new ArrayList<>(filterTable.keySet());

            printTable(filterTable, addRows);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Print the table
     *
     * @param filterTable filter table
     * @param addRows     rows to add
     */
    private void printTable(LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable, List<Integer> addRows) {

        for (String header : selectColumnList) {
            System.out.print(header);
            for (int i = 0; i < 15 - header.length(); i++)
                System.out.print(" ");
        }
        System.out.println();

        for (int i = 0; i < 15 * selectColumnList.size(); i++)
            System.out.print("-");

        System.out.println();

        for (Integer id : filterTable.keySet()) {
            if (addRows.contains(id)) {
                LinkedHashMap<String, String> row = filterTable.get(id);
                for (String header : selectColumnList) {
                    String value = row.get(header.trim());
                    System.out.print(value);
                    for (int i = 0; i < 15 - value.length(); i++)
                        System.out.print(" ");
                }
                System.out.println();
            }
        }
    }

    /**
     * validates user input
     *
     * @return validation status
     */
    private boolean validate() {

        if (!queryString.contains("select"))
            return false;

        else if (!queryString.contains("from"))
            return false;

        else if (!queryString.matches("^[a-zA-Z0-9(-_) *<>=']+$"))
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

            query = query.replace("select", "");
            int startIndex = 0;
            int endIndex = query.indexOf("from", startIndex);
            String requiredColumns = query.substring(startIndex, endIndex);

            startIndex = query.indexOf("from") + 4;
            endIndex = (query.contains("where")) ? query.indexOf("where") : query.length();

            String tableName = query.substring(startIndex, endIndex).trim();

            if (!tableName.matches("^[a-zA-Z][a-zA-Z0-9-_]+$"))
                throw new Exception("parsing error");

            dataSource.setTableName(tableName);

            ArrayList<String> allColumns = new ArrayList<>();
            dataSource.getColumnNames(allColumns);

            if (query.contains("*") || query.contains("all")) {
                selectColumnList = allColumns;
            } else {
                selectColumnList = Arrays.asList(requiredColumns.replaceAll(" ", "").split(","));

                for (String col : selectColumnList) {
                    if (!allColumns.contains(col.trim().toLowerCase())) {
                        throw new Exception("Column doesn't exists.");
                    } else
                        col = col.toLowerCase().trim();
                }
            }

            if (query.contains("where")) {
                whereClause = query.substring(query.indexOf("where") + 5);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
