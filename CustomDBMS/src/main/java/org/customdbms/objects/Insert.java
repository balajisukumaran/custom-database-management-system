package org.customdbms.objects;

import org.customdbms.common.Constants;
import org.customdbms.data.DataSource;

import java.util.*;

/**
 * Insert query class
 */
public class Insert extends Query {

    String tableName;
    LinkedHashMap<String, String> columnsAndValues;

    /**
     * constructor for insert query class
     *
     * @param queryString query string
     * @param buffer      transaction buffer
     */
    public Insert(String queryString, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        columnsAndValues = new LinkedHashMap<>();
        setup(queryString);
        dataSource.setupDB();
        DataSource.buffer = buffer;
    }

    /**
     * execute logic for insert query
     *
     * @return execution status
     */
    @Override
    public boolean execute() {
        try {
            validate();
            parse();
            dataSource.setTableName(tableName);
            if (dataSource.insertData(columnsAndValues)) {
                DataSource.appendExecutionMessage("Record inserted successfully");
            } else
                System.out.println(Constants.ERR_NOT_WELLFORMMED);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * validates user input
     *
     * @return validation status
     */
    private boolean validate() {

        if (!queryString.contains("insert into"))
            return false;

        else if (!queryString.matches("^[a-zA-Z0-9(-_) ']+$"))
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
            query = query.replace("insert into", "");
            int startIndex = 0;
            int endIndex = query.indexOf("(", startIndex);
            tableName = query.substring(startIndex, endIndex).replaceAll(" ", "");

            if (!tableName.matches("^[a-zA-Z][a-zA-Z0-9-_]+$"))
                throw new Exception("parsing error");

            query = query.substring(query.indexOf("(") + 1);

            String[] columnNames = query.substring(0, query.indexOf(")")).split(",");


            if (query.indexOf("values") < 0)
                throw new Exception("parsing error");

            query = query.substring(query.indexOf("(") + 1);
            String[] values = query.substring(0, query.indexOf(")")).split(",");

            if (values.length == columnNames.length)
                for (int i = 0; i < values.length; i++)
                    columnsAndValues.put(columnNames[i].trim(), values[i].trim().replaceAll("'", ""));
            else
                throw new Exception("parsing error");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

