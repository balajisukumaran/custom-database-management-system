package org.customdbms.objects;

import org.customdbms.data.DataSource;

import java.util.*;

/**
 * Query abstract class
 */
public abstract class Query {
    protected DataSource dataSource;
    protected HashMap<Character, Integer> queryCharacterCount;
    protected String queryString;
    protected char[] queryCharacters;

    public abstract boolean execute();

    /**
     * method to calculate character count
     *
     * @param string input string string
     * @return character count
     */
    public HashMap<Character, Integer> characterCount(String string) {
        HashMap<Character, Integer> characterCount = new HashMap();

        for (Character c :
                string.toCharArray()) {
            characterCount.put(c, characterCount.getOrDefault(c, 0) + 1);
        }
        return characterCount;
    }

    /**
     * Query clean up
     *
     * @param queryString user query string
     */
    protected void setup(String queryString) {
        dataSource = new DataSource();
        this.queryString = queryString.trim().toLowerCase();
        this.queryString = this.queryString.replaceAll("  ", " ");
        this.queryString = this.queryString.replaceAll(";", "");
        queryCharacterCount = characterCount(this.queryString);
        queryCharacters = this.queryString.toCharArray();
    }
}