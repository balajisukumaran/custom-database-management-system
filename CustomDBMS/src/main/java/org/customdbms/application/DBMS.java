package org.customdbms.application;

import java.util.ArrayList;
import java.util.Scanner;

import org.customdbms.common.Constants;
import org.customdbms.services.MenuService;
import org.customdbms.services.AuthenticationService;
import org.customdbms.services.Transaction;

/**
 * Database management system
 */
public class DBMS {

    /**
     * Main method
     *
     * @param args command line argument input
     */
    public static void main(String[] args) {
        DBMS.run();
    }

    /**
     * runs the application
     */
    public static void run() {
        Scanner sc = new Scanner(System.in);
        AuthenticationService authentication = new AuthenticationService();
        int option;

        String tryAgain = Constants.OPT_NO;

        do {

            //MAIN MENU
            MenuService.mainMenu();
            option = sc.nextInt();
            sc.nextLine();

            try {

                //LOGIN OPTION
                if (option == 1) {

                    String[] credentials = MenuService.credentialMenu(sc, authentication.generateCaptcha(), Constants.LOGINMENUTEXT);
                    boolean authenticated = authentication.validateUser(credentials[0], credentials[1]);

                    //AUTHENTICATION SUCCESSFUL
                    if (authenticated) {

                        System.out.println(Constants.MSG_LOGGEDINSUCCESSFULLY);
                        sc.nextLine();
                        String executeANewQuery = Constants.OPT_NO;

                        do {
                            System.out.println();

                            try {
                                ArrayList<String> userInput = MenuService.queryMenu(sc);
                                Transaction t = Transaction.getInstance();
                                t.executeQuries(userInput);

                                System.out.println(Constants.MSG_TRYAGAIN);
                                executeANewQuery = sc.nextLine();
                                option = 0;
                            } catch (Exception e) {
                                option = Constants.OPT_ERR;
                                break;
                            }

                        } while (executeANewQuery.equals(Constants.OPT_YES));

                    }

                    //IN-CORRECT CRED
                    else {
                        System.out.println(Constants.MSG_INCORRECTCRED);
                        option = Constants.OPT_ERR;
                    }

                }
                //REGISTER OPTION
                else if (option == 2) {

                    String[] credentials = MenuService.credentialMenu(sc, authentication.generateCaptcha(), Constants.REGISTRATIONMENU);
                    boolean savedSuccessfully = authentication.registerUser(credentials[0], credentials[1]);

                    //REGISTRATION INFO SAVED SUCCESSFULLY
                    if (savedSuccessfully) {
                        System.out.println(Constants.MSG_REGISTEREDSUCCESSFULLY);
                        sc.nextLine();
                        option = Constants.OPT_ERR;
                    }
                    //ERROR OCCURRED
                    else {
                        System.out.println(Constants.MSG_ERR_TRYAGAIN);
                        option = Constants.OPT_ERR;
                    }
                }
                //INVALID OPTION TRY AGAIN
                else {
                    System.out.println(Constants.MSG_INVALID_OPT_TRYAGAIN);
                    tryAgain = sc.nextLine();
                }
            } catch (Exception ex) {
                if (!ex.getMessage().equals(Constants.MSG_INVALID_CAPTCHA))
                    System.out.println(Constants.MSG_ERROROCCURRED);

                System.out.println();
                System.out.println(Constants.MSG_OPENMAINMENU);
                tryAgain = sc.nextLine();
            }
        } while (option == Constants.OPT_ERR || tryAgain.equals(Constants.OPT_YES));
    }
}