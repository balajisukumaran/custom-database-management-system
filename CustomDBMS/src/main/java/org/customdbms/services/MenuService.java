package org.customdbms.services;

import org.customdbms.common.Constants;
import org.customdbms.common.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Menu logic for the application
 */
public class MenuService {
    public static void mainMenu() {

        Map<Integer, String> mainMenu = new HashMap<>();
        mainMenu.put(1, Constants.LOGINTEXT);
        mainMenu.put(2, Constants.REGISTERTEXT);
        mainMenu.put(3, Constants.EXIT);
        System.out.println(Helper.generateMenu(mainMenu, Constants.ENTER_OPTION, true));
    }

    /**
     * displays credential menu
     *
     * @param sc        scanner object
     * @param captcha   captcha to display
     * @param subHeader subheader to display
     * @return user inputs
     * @throws Exception throws exception
     */
    public static String[] credentialMenu(Scanner sc, String captcha, String subHeader) throws Exception {

        String captchaInput;
        String tryAgain;
        String userName;
        String password;

        do {
            System.out.println(Helper.generateHeader(Constants.HEADER));
            System.out.println(Helper.generateSubHeader(subHeader));

            System.out.print(Constants.ENTER_UN);
            userName = sc.nextLine();

            System.out.print(Constants.ENTER_PWD);
            password = sc.nextLine();

            System.out.println("\n" + captcha + "\n");

            System.out.print(Constants.ENTER_CAPTCHA);
            captchaInput = sc.nextLine();

            if (captchaInput.equals(captcha))
                return new String[]{userName, password};

            System.out.print(Constants.MSG_INVALID_CATCHA);
            tryAgain = sc.nextLine();

        } while (tryAgain.equals(Constants.OPT_YES));

        throw new Exception(Constants.MSG_INVALID_CAPTCHA);
    }

    /**
     * Query window menu
     *
     * @param sc scanner menu object
     * @return user query input
     */
    public static ArrayList<String> queryMenu(Scanner sc) {

        ArrayList<String> query = new ArrayList<String>();

        System.out.println(Helper.generateSubHeader(Constants.QUERY_WINDOW));
        System.out.println(Constants.TYPE_Q_ENTER);
        Scanner s = new Scanner(System.in);

        while (s.hasNextLine() == true) {
            String input = s.nextLine();

            if (input == null || input.isEmpty())
                break;

            query.add(input);
        }

        return query;
    }
}
