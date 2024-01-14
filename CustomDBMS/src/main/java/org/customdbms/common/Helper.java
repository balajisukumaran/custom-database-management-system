package org.customdbms.common;

import java.util.*;

/**
 * Helper method to generate the menu
 */
public class Helper {

    /**
     * Generated the header in consistent format
     *
     * @param text Header text
     * @return formatted header text
     */
    public static String generateHeader(String text) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append(Constants.MENU_SEPERATOR);
        sb.append("\n").append(text).append("\n");
        sb.append(Constants.MENU_SEPERATOR);

        return String.valueOf(sb);
    }

    /**
     * Generated the sub header in a consistent format
     *
     * @param text sub header text
     * @return formatter sub header
     */
    public static String generateSubHeader(String text) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append("\n").append(text).append("\n");
        sb.append(Constants.MENU_SEPERATOR);
        sb.append("\n");
        return String.valueOf(sb);
    }

    /**
     * Menu for the DBMS System
     *
     * @param options             Available options
     * @param Message             message to display after menu
     * @param displayOptionNumber y/n to display the option number
     * @return return the menu string
     */
    public static String generateMenu(Map<Integer, String> options, String Message, boolean displayOptionNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateHeader(Constants.HEADER));
        sb.append("\n");

        for (int id : options.keySet()) {
            if (displayOptionNumber)
                sb.append(id).append(". ").append(options.get(id));
            else
                sb.append(options.get(id));
            sb.append("\n");
        }

        sb.append("\n");
        sb.append(Message);

        return String.valueOf(sb);
    }
}
