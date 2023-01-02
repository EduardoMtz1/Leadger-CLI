/*
 * Ledger.java
 * Main class
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Ledger {
    final List<String> comments = Arrays.asList(";", "#", "%", "|", "*"); // comments: List of symbols supported for
                                                                                // comments in CLI
    public File indexFile; // indexFile: File where we can find the include for the ledger files
    public String routeName = ""; // routeName: Name of the route of the indexFile
    public File priceDBFile; // priceDBFile: File where we can find the exchanges.
    public List<String> accounts = new ArrayList<String>(); // accounts: List of the name of the files included on Index
                                                            // file
    public String sufix; // sufix: Sufix of the index file, e.g. .ledger, .dat
    public static final String ANSI_RED = "\u001B[31m"; //
    public static final String ANSI_BLUE = "\u001B[34m"; // ANSI_RED, ANSI_BLUE, ANSI_RESET: Colours to print text in
                                                         // CLI
    public static final String ANSI_RESET = "\u001B[0m"; //

    /*
     * Funtion: errorHandling
     * Parameter: 
     *      String s: Error happened
     * 
     * This function closes the program when an error ocurred and prints s, where
     * the error happened is especified.
     */
    void errorHandling(String s) {
        System.out.println(s);
        System.exit(-1);
    }

    /*
     * Function: getRegistries
     * Parameter: 
     *      String arg: Name to find on the registries
     * Returns: List of registries
     * 
     * This function search for the registries to use on the ledger files, searching
     * for the especified name and returning a list of the matchong registries.
     * If no name is given, returns thelist of every registry on the ledger files.
     */
    List<Registry> getRegistries(String arg) {
        List<Registry> registriesToUse = new ArrayList<Registry>();
        for (int i = 0; i < accounts.size(); i++) { //Iterate on the list of the files names
            String routeAc = routeName + "/" + accounts.get(i) + "." + sufix;
            File account = new File(routeAc);
            try {   
                Scanner fileScanner = new Scanner(account); //Creating scanner for the file
                List<String> rawRegistry;
                while (fileScanner.hasNextLine()) {         //Staring to read the file
                    String line = fileScanner.nextLine();
                    if (line.charAt(0) == ';')
                        line = fileScanner.nextLine();
                    while (fileScanner.hasNextLine()) {
                        if (Pattern.compile("^[0-9].*$").matcher(line).matches()) {
                            rawRegistry = new ArrayList<String>();      //Creating new rawRegistry, a list of Strings for the registry
                            rawRegistry.add(line);
                            boolean isDate = false;
                            while (fileScanner.hasNextLine() && !isDate) {  //Reading all the registry
                                line = fileScanner.nextLine();
                                if (!Pattern.compile("^[0-9].*$").matcher(line).matches()) {
                                    rawRegistry.add(line);
                                } else {
                                    isDate = true;
                                }
                            }
                            Registry reg = new Registry(rawRegistry);
                            registriesToUse.add(reg);
                        }
                    }
                }
                fileScanner.close();
            } catch (FileNotFoundException e) {     //Catching in case of file not found
                e.printStackTrace();
                errorHandling("File " + accounts.get(i) + " not found\n" + e.getStackTrace());
            }
        }
        if (!arg.equals("")) {//Case: arg is an account to search
            List<Registry> finalList = new ArrayList<Registry>(); //Creating a new list for the final registries to use
            for (int i = 0; i < registriesToUse.size(); i++) {    //Searching for the arg
                for (int j = 0; j < registriesToUse.get(i).movementsList.size(); j++) {
                    if (registriesToUse.get(i).movementsList.get(j).name.indexOf(arg) != -1)
                        finalList.add(registriesToUse.get(i));
                }
            }
            registriesToUse = finalList;
        }
        return registriesToUse; //Returning the list of registries
    }

    /*
     * Function: printableFunctions
     * Parameters: 
     *      boolean sort: To know if the information needs to be sorted
     *      String sortType: To know if the sort is by date or amount
     *      List<String> actionArgs: List of accounts needed to be found on the registries
     *      String functionToDo: To know whic function we need between print and registry
     * 
     * This function is where we can obtain and print the information for the print and
     * registry functions.
     */
    void printableFunctions(boolean sort, String sortType, List<String> actionArgs, String functionToDo) {
        if (actionArgs.size() == 0) { //Case: No account introduced, we need to show every registry
            List<Registry> registries = getRegistries("");  //Getting the registries from getRegistries()
            if (sort) { //Case: Sort needed
                if (sortType.toUpperCase().equals("DATE") || sortType.toUpperCase().equals("D")) { //Case: Sort by date
                    Collections.sort(registries, (a, b) -> {    //Sorting the list by date
                        return a.date.compareTo(b.date);
                    });
                }
                if (sortType.toUpperCase().equals("AMOUNT") || sortType.toUpperCase().equals("A")) { //Case: Sort by amount
                    Collections.sort(registries, (a, b) -> {    //Sorting the list by amount
                        return (int) (a.getAmount(priceDBFile) - b.getAmount(priceDBFile));
                    });
                }
            }
            for (int i = 0; i < registries.size(); i++) {//Print the registries
                if (functionToDo.equals("reg"))
                    registries.get(i).regPrint();       //Case: Registry function
                else
                    registries.get(i).plainPrint();     //Case: Print function
            }
        } else { //Case: Accounts introduced, we need to find the registries that match
            for (int i = 0; i < actionArgs.size(); i++) {
                List<Registry> registries = getRegistries(actionArgs.get(i)); //Getting the registries from getRegistries()
                System.out.println(actionArgs.get(i));
                if (sort) {
                    if (sortType.toUpperCase().equals("DATE") || sortType.toUpperCase().equals("D")) { //Case: Sort by date
                        Collections.sort(registries, (a, b) -> { //Sorting the list by date
                            return a.date.compareTo(b.date);
                        });
                    }
                    if (sortType.toUpperCase().equals("AMOUNT") || sortType.toUpperCase().equals("A")) { //Case: Sort by amount
                        Collections.sort(registries, (a, b) -> { //Sorting the list by amount
                            return (int) (a.getAmount(priceDBFile) - b.getAmount(priceDBFile));
                        });
                    }
                }
                for (int j = 0; j < registries.size(); j++) { //Print the registries
                    if (functionToDo.equals("reg"))
                        registries.get(j).regPrint();         //Case: Registry function
                    else
                        registries.get(j).plainPrint();       //Case: Print function
                }

            }
        }
    }

    /*
     * Function: printableFAccount
     * Parameters: 
     *      String accountName: Name of the account to print
     *      List<Counting> counting: Movements and amount from an account
     *      int Spaces: Number of spaces to format print
     * 
     * This function prints the values of the balance function
     */
    void printableAccount(String accountName, List<Counting> counting, int spaces) {
        String finalName = "";
        for (int i = 0; i < spaces; i++) {
            finalName = finalName + "\t";
        }
        finalName = finalName + accountName;    //Creating final string of the name to print
        DecimalFormat df = new DecimalFormat("#.00");   
        for (int i = 0; i < counting.size(); i++) {
            String finalAmount = df.format(counting.get(i).getTotal()); //Formating the amount of the account
            if (counting.get(i).currency.equals("$"))
                finalAmount = counting.get(i).currency + finalAmount;
            else
                finalAmount = finalAmount + " " + counting.get(i).currency;
            if (counting.get(i).getTotal() > 0) //Case: Amount > 0
                System.out.printf("%15s", finalAmount); //Print the amount
            else    //Case: Amount > 0
                System.out.printf(ANSI_RED + "%15s" + ANSI_RESET, finalAmount); //Print the amount in red
            if (i == counting.size() - 1)
                System.out.print("  " + ANSI_BLUE + finalName + ANSI_RESET + "\n"); //Print the name of the account
            else
                System.out.print("\n");
        }
    }

    /*
     * Function: balanceFunction
     * Parameters: 
     *      List<String> actionArgs: List of accounts needed to be found on the registries
     * 
     * This function is where we can obtain and print the balance function values
     */
    void balanceFunction(List<String> actionArgs) {
        List<Movement> movements = new ArrayList<Movement>();   //Starting the list of movements we'll work with
        if (actionArgs.size() == 0) {
            List<Registry> registries = getRegistries("");  //Getting the list of registries
            for (int j = 0; j < registries.size(); j++) {
                double finalAmount = 0; //Amount for the last movement of the registry if it doesn't have one
                for (int k = 0; k < registries.get(j).movementsList.size(); k++) {
                    if (k < registries.get(j).movementsList.size() - 1) {
                        finalAmount = finalAmount + registries.get(j).movementsList.get(k).amount;
                    } else {
                        finalAmount = registries.get(j).movementsList.get(k).amount != 0    //Asking if the last movement of a registry has an amount
                                && !registries.get(j).movementsList.get(k).currency.equals("")
                                        ? registries.get(j).movementsList.get(k).amount
                                        : finalAmount + registries.get(j).movementsList.get(k).amount;
                    }
                    Movement regMov = registries.get(j).movementsList.get(k);
                    if (regMov.amount == 0 && regMov.currency.equals("")) {
                        regMov.amount = finalAmount * -1;
                        regMov.currency = registries.get(j).movementsList.get(0).currency;
                    }
                    movements.add(regMov);  //Adding the movement to the working list
                }
            }
        } else {
            for (int i = 0; i < actionArgs.size(); i++) {
                List<Registry> registries = getRegistries(actionArgs.get(i)); //Getting the list of registries
                for (int j = 0; j < registries.size(); j++) {
                    double finalAmount = 0; //Amount for the last movement of the registry if it doesn't have one
                    for (int k = 0; k < registries.get(j).movementsList.size(); k++) {
                        if (k < registries.get(j).movementsList.size() - 1) {
                            finalAmount = finalAmount + registries.get(j).movementsList.get(k).amount;
                        } else {
                            finalAmount = registries.get(j).movementsList.get(k).amount != 0    //Asking if the last movement of a registry has an amount
                                    && !registries.get(j).movementsList.get(k).currency.equals("")
                                            ? registries.get(j).movementsList.get(k).amount
                                            : finalAmount + registries.get(j).movementsList.get(k).amount;
                        }
                        if (registries.get(j).movementsList.get(k).name.indexOf(actionArgs.get(i)) != -1) {
                            Movement regMov = registries.get(j).movementsList.get(k);
                            if (regMov.amount == 0 && regMov.currency.equals("")) {
                                regMov.amount = finalAmount * -1;
                                regMov.currency = registries.get(j).movementsList.get(0).currency;
                            }
                            movements.add(regMov);  //Adding the movement to the working list
                        }
                    }
                }
            }
        }

        printBalance("", 0, movements);  //Calling printBalance Function 

        List<Counting> countings = new ArrayList<Counting>();
        List<String> currencies = new ArrayList<String>();
        for (int i = 0; i < movements.size(); i++) {    //Getting the amounts and currencies for final balance
            if (!currencies.contains(movements.get(i).currency)) {
                currencies.add(movements.get(i).currency);
                Counting cCur = new Counting(movements.get(i).currency);
                for (int j = 0; j < movements.size(); j++) {
                    if (movements.get(j).currency.equals(cCur.currency))
                        cCur.movements.add(movements.get(j));
                }
                countings.add(cCur);
            }
        }
        System.out.println("--------------------------------------");
        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < countings.size(); i++) {    //Printing final balance for every currency we have
            String finalAmount = df.format(countings.get(i).getTotal());
            if (countings.get(i).currency.equals("$"))
                finalAmount = countings.get(i).currency + finalAmount;
            else
                finalAmount = finalAmount + " " + countings.get(i).currency;
            if (countings.get(i).getTotal() > 0)
                System.out.printf("%15s\n", finalAmount);
            else
                System.out.printf(ANSI_RED + "%15s\n" + ANSI_RESET, finalAmount);
        }

    }

    /*
     * Function: balanceFunction
     * Parameters: 
     *      String lastName: Name of the accounts to print before the actual account
     *      int iterations: Iteration of the called function
     *      List<Movement> movements: List of movements to work with
     * 
     * This function separates the movements by the name of the account. If an account have subaccounts
     * the function is called again with a new list of movements matching the name of the first account.
     * 
     * At the end we have the accounts separated and the info printed.
     * 
     * Recursive function
     */
    void printBalance(String lastName, int iterations, List<Movement> movements) {
        List<Account> accountsBalance = new ArrayList<Account>();   //Creating a list of the accounts
        List<String> accountsName = new ArrayList<String>();    //Creating a list with the accounts name
        for (int i = 0; i < movements.size(); i++) {    //Iterating every movement
            String[] acc = movements.get(i).name.trim().split("\\:");
            if (iterations < acc.length && !accountsName.contains(acc[iterations])) {
                accountsName.add(acc[iterations]);  //Adding the account name to the list if missed
                Account tempAcc = new Account(acc[iterations]); //Finding all the movements for the account
                for (int j = 0; j < movements.size(); j++) {
                    String[] nAcc = movements.get(j).name.trim().split("\\:");
                    if (iterations < acc.length && iterations < nAcc.length && nAcc[iterations].equals(acc[iterations]))
                        tempAcc.movements.add(movements.get(j));
                }
                accountsBalance.add(tempAcc);
            }
        }

        for (int i = 0; i < accountsBalance.size(); i++) {  //Iterating on every account found
            if (accountsBalance.get(i).movements.size() > 1) {  //Case: More than one movement in the account
                List<String> tempAccountsName = new ArrayList<String>();
                int movementsUsed = 0;
                for (int j = 0; j < accountsBalance.get(i).movements.size(); j++) {     //Getting the name of the subaccounts of the account
                    String[] acc = accountsBalance.get(i).movements.get(j).name.trim().split("\\:");
                    if (iterations + 1 < acc.length && !tempAccountsName.contains(acc[iterations + 1]))
                        tempAccountsName.add(acc[iterations + 1]);
                    if (iterations + 1 < acc.length && tempAccountsName.contains(acc[iterations + 1]))
                        movementsUsed++;
                }
                if (tempAccountsName.size() > 1) {  //Case: More than one sub account
                    accountsBalance.get(i).getAmounts();
                    printableAccount(lastName + accountsBalance.get(i).name, accountsBalance.get(i).countings,
                            iterations);    //Printing the amount of the account
                    printBalance(lastName, iterations + 1, accountsBalance.get(i).movements);   //Calling the function for the subaccounts
                } else if (tempAccountsName.size() == 1) {  //Case: Only one sub account
                    if (movementsUsed < accountsBalance.get(i).movements.size()) {
                        accountsBalance.get(i).getAmounts();
                        printableAccount(lastName + accountsBalance.get(i).name, accountsBalance.get(i).countings,
                                iterations);    //Printing the amount of the account
                        printBalance("", iterations + 1,  accountsBalance.get(i).movements); //Calling the function for the subaccount
                    } else
                        printBalance(lastName + accountsBalance.get(i).name + ":", iterations + 1, 
                                accountsBalance.get(i).movements);      //Calling the function for the subaccount
                } else {    //Case: No subaccounts
                    accountsBalance.get(i).getAmounts();
                    String[] acc = lastName.split("\\:");
                    printableAccount(lastName + accountsBalance.get(i).name, accountsBalance.get(i).countings,
                            iterations - acc.length);
                }
            } else {    //Case: Only one movement in the account
                String finalName = "";
                String[] nameParts = accountsBalance.get(i).movements.get(0).name.trim().split("\\:");
                for (int j = iterations; j < nameParts.length; j++)
                    finalName = finalName + nameParts[j] + ":";     //Getting the final name to print
                accountsBalance.get(i).getAmounts();
                printableAccount(finalName.substring(0, finalName.length() - 1), accountsBalance.get(i).countings,
                        iterations);    //Printing name and amount of the account
            }
        }
    }
    /*
     * Main
     */
    public static void main(String[] args) {
        String priceDB = "", index = "", sortBy = "", action = "";
        Boolean sort = false, actionReady = false;
        Ledger ledger = new Ledger();
        List<String> actionArgs = new ArrayList<String>();
        if (args.length == 0) {        //Case: No args found
            ledger.errorHandling("No arguments found");
        }
        for (int i = 0; i < args.length; i++) {     //Iterating on the args
            if (args[i].trim().equals("--price-db")) {      //Case: No arg for price-db file 
                actionReady = false;
                if (i + 1 == args.length)
                    ledger.errorHandling("No price-db file defined");
                priceDB = args[i + 1].trim();
            }
            if (args[i].trim().equals("-f") || args[i].trim().equals("--file")) {   //Case: No arg for index ledger file 
                actionReady = false;
                if (i + 1 == args.length)
                    ledger.errorHandling("No file defined");
                index = args[i + 1].trim();
            }
            if (args[i].trim().equals("-s") || args[i].trim().equals("--sort")) {   //Case: Sort flag used
                actionReady = false;
                sort = true;
                if (i + 1 == args.length)
                    ledger.errorHandling("No sort mode defined");   //Case: No sort type defined
                sortBy = args[i + 1].trim();
            }
            if (ledger.comments.contains(args[i].trim()))   //Case: Comment
                actionReady = false;
            if (actionReady)    //Case: Name of an account to show
                actionArgs.add(args[i].trim());
            if (args[i].trim().equals("bal") || args[i].trim().equals("balance")) {     //Case: Balance function
                action = "bal";
                actionReady = true;
            }
            if (args[i].trim().equals("reg") || args[i].trim().equals("registry")) {    //Case: Registry function
                action = "reg";
                actionReady = true;
            }
            if (args[i].trim().equals("print")) {   //Case: Print function
                action = "print";
                actionReady = true;
            }

        }

        ledger.indexFile = new File(index);
        if (!ledger.indexFile.exists()) {   //Case: Index ledger file not found
            ledger.errorHandling("File " + index + " not found");
        }
        ledger.routeName = index.replaceAll(ledger.indexFile.getName(), "");
        ledger.priceDBFile = new File(priceDB);
        if (!ledger.priceDBFile.exists()) { //Case: price-db file not found
            ledger.errorHandling("File " + priceDB + " not found");
        }

        try {   //Getting all the ledger files from the index
            Scanner fileRead = new Scanner(ledger.indexFile);
            while (fileRead.hasNextLine()) {
                String lineRead = fileRead.nextLine();
                String[] sliced = lineRead.split("\\s");
                if (sliced[0].trim().equals("!include")) {
                    String[] name = sliced[1].split("\\.");
                    ledger.accounts.add(name[0].trim());
                    if (ledger.sufix == null)
                        ledger.sufix = name[1].trim();
                }
            }
            fileRead.close();
        } catch (FileNotFoundException e) { //Catch if Index file not found
            ledger.errorHandling("File " + index + " not found\n" + e.getStackTrace());
        }

        if (action.equals(""))  //Case: No function defined
            ledger.errorHandling("No action defined");
        if (action.equals("bal"))   //Case: Balance function
            ledger.balanceFunction(actionArgs);
        if (action.equals("reg") || action.equals("print")) //Case: Print or registry function
            ledger.printableFunctions(sort, sortBy, actionArgs, action);
    }
}