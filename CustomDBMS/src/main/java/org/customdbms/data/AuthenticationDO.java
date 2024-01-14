package org.customdbms.data;

import org.customdbms.common.Constants;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * Authentication data access object class
 */
public class AuthenticationDO {

    File credentialFile;
    String credentialPath;

    /**
     * Constructor for authentication data access object
     */
    public AuthenticationDO() {
        credentialPath = Constants.DATAPATH + Constants.SYSTEMFILES + Constants.CREDENTIALS;
        fileSetup();
    }

    /**
     * set's up the credential file
     */
    private void fileSetup() {
        try {
            credentialFile = new File(credentialPath);

            if (!credentialFile.exists())
                credentialFile.mkdirs();

            credentialFile = new File(credentialPath + Constants.CREDENTIAL_FILENAME);

            credentialFile.createNewFile();
        } catch (Exception e) {
            System.out.println("Error occurred.");
        }
    }

    /**
     * Validates the user
     *
     * @param username       username input
     * @param hashedPassword hashed password of user
     * @return authenticated or not
     */
    public boolean validateUser(String username, String hashedPassword) {

        try {
            Scanner sc = new Scanner(credentialFile);
            while (sc.hasNextLine()) {
                String data = sc.nextLine();
                String[] credential = data.split(",");
                if (credential[0].equals(username) && credential[1].equals(hashedPassword)) {
                    sc.close();
                    return true;
                }
            }
            sc.close();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * registers a new user
     *
     * @param username       username given by the user
     * @param hashedPassword password given by user
     * @return true in case of no error else false
     */
    public boolean registerUser(String username, String hashedPassword) {
        try {
            FileWriter myWriter = new FileWriter(credentialPath + Constants.CREDENTIAL_FILENAME, true);
            myWriter.write(username + "," + hashedPassword);
            myWriter.write(System.getProperty(Constants.LINE_SEPARATOR));
            myWriter.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
