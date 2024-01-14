package org.customdbms.data;

import org.customdbms.common.Constants;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Data source class for the DBMS system
 */
public class DataSource {
    private String fileName;
    private String metaDataFileName;
    File file;
    File metaFile;
    String tablePath = Constants.DATAPATH;
    String metaDataPath = Constants.DATAPATH + Constants.SYSTEMFILES + Constants.METADATA;
    public LinkedHashMap<Integer, LinkedHashMap<String, String>> table;
    public static HashMap<String, LinkedHashMap<Integer, LinkedHashMap<String, String>>> buffer;
    public static LinkedList<String> executionMessages = new LinkedList<>();
    private String databaseName;

    /**
     * persist the buffer data
     *
     * @return true in case of no error else false
     */
    public boolean commitBuffer() {
        if (buffer != null) {
            try {
                for(String table: buffer.keySet()){

                    LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable = buffer.get(table);
                    String tableFilePath = tablePath + table;
                    File tableFile = new File(tableFilePath);
                    Scanner sc = new Scanner(tableFile);
                    if (sc.hasNextLine()) {
                        String headersLine = sc.nextLine();
                        ArrayList<StringBuilder> rows = new ArrayList<>();

                        for (Integer id : filterTable.keySet()) {
                            StringBuilder newRecord = new StringBuilder();
                            LinkedHashMap<String, String> row = filterTable.get(id);

                            for (String key : row.keySet()) {
                                newRecord.append(row.get(key)).append(Constants.PILCROW);
                            }
                            rows.add(new StringBuilder(newRecord.substring(0, newRecord.length() - 1)));
                        }
                        FileWriter myWriter = new FileWriter(tableFilePath);
                        myWriter.write(headersLine);
                        for (StringBuilder row : rows) {
                            myWriter.write(System.getProperty(Constants.LINE_SEPARATOR));
                            myWriter.write(row.substring(0, row.length()));
                        }
                        myWriter.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception occurred when committing buffered changes.");
                return false;
            }
        }
        for (String message : executionMessages)
            System.out.println(message);
        return true;
    }

    /**
     * Clear buffered data
     *
     * @return true in case of no error else false
     */
    public static boolean clearBuffer() {
        if (buffer != null) {
            try {
                DataSource.executionMessages = new LinkedList<>();
                DataSource.buffer = new HashMap<>();
            } catch (Exception e) {
                System.out.println("Exception occurred when clearing buffer");
                return false;
            }
        }
        return true;
    }

    /**
     * append execution message
     *
     * @param message message to append
     */
    public static void appendExecutionMessage(String message) {
        DataSource.executionMessages.add("\n");
        DataSource.executionMessages.add(message);
        DataSource.executionMessages.add("\n");
    }

    /**
     * set the table name and table metadata and create the required files
     *
     * @param fileName table name (stored as a file in the same name)
     */
    public void setTableName(String fileName) {
        try {
            this.fileName = tablePath;
            file = new File(this.fileName);

            if (!file.exists())
                file.mkdirs();

            this.fileName = this.fileName + fileName + ".table";

            file = new File(this.fileName);
            file.createNewFile();

            //meta-datafile creation
            this.metaDataFileName = metaDataPath;
            metaFile = new File(this.metaDataFileName);
            if (!metaFile.exists())
                metaFile.mkdirs();

            this.metaDataFileName = this.metaDataFileName + "." + fileName;

            metaFile = new File(this.metaDataFileName);
            metaFile.createNewFile();
        } catch (Exception e) {
        }
    }

    /**
     * Build the buffer table
     */
    public void buildTable() {
        try {
            if (buffer != null && buffer.containsKey(file.getName()))
                table = buffer.get(file.getName());
            else {
                Scanner sc = new Scanner(file);
                if (sc.hasNextLine()) {
                    table = new LinkedHashMap<>();
                    String headersLine = sc.nextLine();
                    String[] headers = headersLine.split(Constants.PILCROW);
                    int i = 0;
                    while (sc.hasNextLine()) {
                        table.put(i, new LinkedHashMap<>());
                        String[] record = sc.nextLine().split(Constants.PILCROW);

                        for (int j = 0; j < record.length; j++)
                            table.get(i).put(headers[j], record[j]);
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
    }

    /**
     * create the columns for the table
     *
     * @param columns columns with metadata
     * @return true in case of no error else false
     */
    public boolean createColumns(LinkedHashMap<String, List<String>> columns) {
        try {
            if (file.exists() && metaFile.exists()) {
                FileWriter myWriter = new FileWriter(fileName);
                StringBuilder row = new StringBuilder();

                for (String key : columns.keySet()) {
                    row.append(key).append(Constants.PILCROW);
                }

                myWriter.write(row.substring(0, row.length() - 1));
                myWriter.close();

                myWriter = new FileWriter(metaDataFileName);

                row = new StringBuilder();
                for (String key : columns.keySet())
                    row.append(key).append(": ").append(String.join(Constants.PILCROW, columns.get(key))).append(System.getProperty(Constants.LINE_SEPARATOR));

                myWriter.write(String.valueOf(row));
                myWriter.close();
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Insert data into the table or buffer
     *
     * @param columnsAndValues column and values
     * @return true in case of no error else false
     */
    public boolean insertData(LinkedHashMap<String, String> columnsAndValues) {
        try {
            if (buffer != null) {
                if(buffer.containsKey(file.getName()))
                {
                    table = buffer.get(file.getName());
                    table.put(table.size(), columnsAndValues);
                }
                else{
                    buffer.put(file.getName(), new LinkedHashMap<>());
                    table = buffer.get(file.getName());
                    table.put(table.size(), columnsAndValues);
                }
            } else if (file.exists()) {
                Scanner sc = new Scanner(file);
                if (sc.hasNextLine()) {
                    String headersLine = sc.nextLine();
                    String[] headers = headersLine.split(Constants.PILCROW);

                    StringBuilder newRecord = new StringBuilder();

                    for (String header : headers) {
                        if (columnsAndValues.containsKey(header.trim())) {
                            newRecord.append(columnsAndValues.get(header)).append(Constants.PILCROW);
                        } else
                            return false;
                    }

                    FileWriter myWriter = new FileWriter(fileName, true);
                    myWriter.write(System.getProperty(Constants.LINE_SEPARATOR));
                    myWriter.write(newRecord.substring(0, newRecord.length() - 1));
                    myWriter.close();

                    if (buffer != null) {
                        buildTable();
                        table.put(table.size() + 1, columnsAndValues);
                        buffer.put(file.getName(), table);
                    }
                }
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Gets the column names of the table
     *
     * @param columns columns in the table
     * @return true in case of no error else false
     */
    public boolean getColumnNames(ArrayList<String> columns) {
        try {
            Scanner sc = new Scanner(file);
            if (sc.hasNextLine()) {
                String[] headerLine = sc.nextLine().split(Constants.PILCROW);

                for (String header : headerLine)
                    columns.add(header);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * set the database name
     *
     * @param dbName db name
     * @return set status
     */
    public boolean setDBName(String dbName) {
        try {
            File file = new File(Constants.DATAPATH + dbName + "/");

            if (!file.exists())
                file.mkdirs();

            databaseName = dbName;
            tablePath = tablePath + databaseName + "/";
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * set up the database
     *
     * @return setup status
     */
    public boolean setupDB() {
        String DBname = null;
        try {
            File file = new File(Constants.DATAPATH);
            File[] listOfFiles = file.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isDirectory() && !listOfFiles[i].getName().toLowerCase().contains("system")) {
                    DBname = listOfFiles[i].getName();
                    break;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return setDBName(DBname);
    }

    public void updateFile(LinkedHashMap<Integer, LinkedHashMap<String, String>> filterTable) {
        if (buffer != null) {
            buffer.put(file.getName(), filterTable);
        } else if (file.exists()) {
            try {

                File tableFile = new File(fileName);
                Scanner sc = new Scanner(tableFile);
                if (sc.hasNextLine()) {
                    String headersLine = sc.nextLine();
                    ArrayList<StringBuilder> rows = new ArrayList<>();

                    for (Integer id : filterTable.keySet()) {
                        StringBuilder newRecord = new StringBuilder();
                        LinkedHashMap<String, String> row = filterTable.get(id);

                        for (String key : row.keySet()) {
                            newRecord.append(row.get(key)).append(Constants.PILCROW);
                        }
                        rows.add(new StringBuilder(newRecord.substring(0, newRecord.length() - 1)));
                    }
                    FileWriter myWriter = new FileWriter(fileName);
                    myWriter.write(headersLine);
                    for (StringBuilder row : rows) {
                        myWriter.write(System.getProperty(Constants.LINE_SEPARATOR));
                        myWriter.write(row.substring(0, row.length()));
                    }
                    myWriter.close();

                }
            } catch (Exception e) {
                System.out.println("update error occurred.");
            }
        }
    }
}