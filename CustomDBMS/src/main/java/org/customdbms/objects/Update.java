package org.customdbms.objects;

import org.customdbms.data.DataSource;

import java.util.*;

/**
 * update query class
 */
public class Update extends Query {
    String tableName;
    HashMap<String, String> columnsAndValues;
    LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable;
    String setStatement;
    String whereClause;

    /**
     * constructor for update query class
     *
     * @param queryString query string
     * @param buffer transaction buffer
     */
    public Update(String queryString, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        columnsAndValues = new HashMap<>();
        setup(queryString);
        dataSource.setupDB();
        DataSource.buffer=buffer;
    }

    /**
     * execute logic for update query
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

            updateDataTable(filterTable, addRows);



            DataSource.appendExecutionMessage("Updated successfully.");

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * update the data table
     *
     * @param filterTable intermediate table
     * @param addRows     rows to add
     */
    private void updateDataTable(LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable, List<Integer> addRows) {

        for (Integer id : filterTable.keySet()) {
            if (addRows.contains(id)) {
                LinkedHashMap<String, String> row = filterTable.get(id);
                for (String header : row.keySet()) {
                    String[] lhsrhs = setStatement.split("=");
                    lhsrhs[0] = lhsrhs[0].replaceAll(" ","");
                    lhsrhs[0] = lhsrhs[0].replaceAll("'","");
                    lhsrhs[1] = lhsrhs[1].replaceAll(" ","");
                    lhsrhs[1] = lhsrhs[1].replaceAll("'","");
                    String value = row.get(lhsrhs[0].trim());

                    if (lhsrhs[1].contains(lhsrhs[0]))
                        lhsrhs[1] = lhsrhs[1].replaceAll(lhsrhs[0], value);

                    applyArithmetic(lhsrhs);
                    row.put(lhsrhs[0].trim(), lhsrhs[1].trim());
                    break;
                }
                dataSource.updateFile(filterTable);
            }
        }
    }

    /**
     * Apply arithmetic logic for value to update
     *
     * @param lhsrhs left hand side and right hand side
     */
    private void applyArithmetic(String[] lhsrhs) {

        if (lhsrhs[1].contains("+")) {
            String[] rhs = lhsrhs[1].split("\\+");
            lhsrhs[1] = String.valueOf((Integer.parseInt(rhs[0]) + Integer.parseInt(rhs[1])));
        } else if (lhsrhs[1].contains("-")) {
            String[] rhs = lhsrhs[1].split("-");
            lhsrhs[1] = String.valueOf((Integer.parseInt(rhs[0]) - Integer.parseInt(rhs[1])));
        } else if (lhsrhs[1].contains("*")) {
            String[] rhs = lhsrhs[1].split("\\*");
            lhsrhs[1] = String.valueOf((Integer.parseInt(rhs[0]) * Integer.parseInt(rhs[1])));
        } else if (lhsrhs[1].contains("/")) {
            String[] rhs = lhsrhs[1].split("/");
            lhsrhs[1] = String.valueOf((Integer.parseInt(rhs[0]) / Integer.parseInt(rhs[1])));
        }
    }


    /**
     * validates user input
     *
     * @return validation status
     */
    private boolean validate() {

        if (!queryString.contains("update"))
            return false;

        else if (!queryString.contains("set"))
            return false;

        else if (!queryString.matches("^[a-zA-Z0-9(_) */+-<>=']+$"))
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

            query = query.replace("update", "");
            int startIndex = 0;
            int endIndex = query.indexOf("set", startIndex);
            tableName = query.substring(startIndex, endIndex).trim();

            if (!tableName.matches("^[a-zA-Z][a-zA-Z0-9-_]+$"))
                throw new Exception("parsing error");

            dataSource.setTableName(tableName);

            startIndex = query.indexOf("set") + 3;
            endIndex = (query.contains("where")) ? query.indexOf("where") : query.length();

            setStatement = query.substring(startIndex, endIndex);

            if (query.contains("where"))
                whereClause = query.substring(query.indexOf("where") + 5);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}