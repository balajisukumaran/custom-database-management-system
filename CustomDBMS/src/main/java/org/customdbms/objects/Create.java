package org.customdbms.objects;

import org.customdbms.common.Constants;
import org.customdbms.data.DataSource;

import java.util.*;

/**
 * Create query class
 */
public class Create extends Query {
    String tableName;
    LinkedHashMap<String, List<String>> columns;

    /**
     * constructor for create query class
     *
     * @param queryString query string
     * @param buffer transaction buffer
     */
    public Create(String queryString, HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer) {
        columns = new LinkedHashMap<>();
        setup(queryString);
        dataSource.setupDB();
        DataSource.buffer=buffer;
    }

    /**
     * execute logic for create statement execute
     *
     * @return execution status
     */
    @Override
    public boolean execute() {
        try {
            validate();
            parse();
            dataSource.setTableName(tableName);
            if (dataSource.createColumns(columns))
                System.out.println(Constants.MSG_TABLECREATED);
            else
                System.out.println(Constants.ERR_NOT_WELLFORMMED);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * validate the input query
     *
     * @return validation status
     */
    private boolean validate() {

        if (!queryString.contains("create table"))
            return false;

        else if (!queryString.matches("^[a-zA-Z0-9(-_) ]+$"))
            return false;

        else if (queryCharacterCount.get("(") != queryCharacterCount.get(")"))
            return false;

        return true;
    }

    /**
     * parse the input query
     *
     * @return parsing status
     */
    private boolean parse() {
        try {
            String query = this.queryString;
            query = query.replace("create table", "");
            int startIndex = 0;
            int endIndex = query.indexOf("(", startIndex);
            tableName = query.substring(startIndex, endIndex).replaceAll(" ", "");

            if (!tableName.matches("^[a-zA-Z][a-zA-Z0-9-_]+$"))
                throw new Exception("parsing error");

            query = query.substring(query.indexOf("(") + 1);

            String[] eachColumnAndTypes = query.substring(0, query.lastIndexOf(")")).split(",");

            for (String eachColumnAndType : eachColumnAndTypes) {

                eachColumnAndType = eachColumnAndType.replace("not null", "not_null");
                eachColumnAndType = eachColumnAndType.replace("primary key", "primary_key");
                eachColumnAndType = eachColumnAndType.replace("foreign key references ", "foreign_key_references_");

                String[] columnAndType = eachColumnAndType.trim().split(" ");
                List<String> columnInfo = new LinkedList<>();

                for (int i = 1; i < columnAndType.length; i++)
                    columnInfo.add(columnAndType[i]);

                columns.put(columnAndType[0], columnInfo);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
