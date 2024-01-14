package org.customdbms.services;

import org.customdbms.common.Constants;
import org.customdbms.data.AuthenticationDO;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Authentication service class
 */
public class AuthenticationService {

    AuthenticationDO authenticationDO;

    /**
     * Constructor to authentication service
     */
    public AuthenticationService() {
        this.authenticationDO = new AuthenticationDO();
    }

    /**
     * Generates Captcha
     *
     * @return captcha for user
     */
    public String generateCaptcha() {

        StringBuilder sb = new StringBuilder(1);

        for (int i = 0; i < 1; i++) {
            int index = (int) (Constants.CAPTCHA_KEYSET.length() * Math.random());
            sb.append(Constants.CAPTCHA_KEYSET.charAt(index));
        }

        return sb.toString();
    }

    /**
     * validates the user
     *
     * @param username username input
     * @param password password input
     * @return true/false validation result
     * @throws UnsupportedEncodingException Unsupported encoding exception
     * @throws NoSuchAlgorithmException     No such algorithm exception
     */
    public boolean validateUser(String username, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hashedPassword = generateHash(password);
        return authenticationDO.validateUser(username, hashedPassword);
    }

    /**
     * registered user
     *
     * @param username username for the user
     * @param password password for the user
     * @return true/false register result
     * @throws UnsupportedEncodingException Unsupported encoding exception
     * @throws NoSuchAlgorithmException     No such algorithm exception
     */
    public boolean registerUser(String username, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hashedPassword = generateHash(password);
        return authenticationDO.registerUser(username, hashedPassword);
    }

    /**
     * generated an hash for given value
     *
     * @param text text to hash
     * @return return hashed value
     * @throws UnsupportedEncodingException Unsupported encoding exception
     * @throws NoSuchAlgorithmException     No such algorithm exception
     */
    public String generateHash(String text) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = text.getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Hash = md.digest(bytesOfMessage);

        return new String(md5Hash, StandardCharsets.UTF_8);
    }
}
