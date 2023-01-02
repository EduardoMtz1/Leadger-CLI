/*
 * Registry.java
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Registry {
    public List<String> rawRegistry; // rawRegistry: List of strings read from the file
    Date date; // date: Date of the registry
    String concept; // concept: Concept of the registry
    List<Movement> movementsList; // movementsList: List of movements of the registry
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
     * Funtion: plaintPrint
     * 
     * This function prints the registry in format of the print funciton, using the
     * raw registry read from the ledger file
     */
    void plainPrint() {
        for (int i = 0; i < rawRegistry.size(); i++)
            System.out.println(rawRegistry.get(i));
    }

    /*
     * Funtion: regPrint
     * 
     * This function prints the registry in format of the registry funciton, using
     * the colors and distribution for this function
     */
    void regPrint() {
        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dateReg = calendar.get(calendar.YEAR) + "-" + months[calendar.get(calendar.MONTH)] + "-"
                + calendar.get(calendar.DAY_OF_MONTH);  //Getting the date from the registry
        String conceptReg = concept;
        if (conceptReg.length() > 27)
            conceptReg = conceptReg.substring(0, 25) + "..";
        System.out.printf("%-11s %-28s", dateReg, conceptReg); //Printing the date
        double finalAmount = 0; //Amount for the last movement of the registry if it doesn't have one
        for (int i = 0; i < movementsList.size(); i++) {
            String movementReg = movementsList.get(i).name;
            if (movementReg.length() > 35)// Case: Formating the name if length > 35
                movementReg = movementReg.substring(0, 33) + "..";
            if (i < movementsList.size() - 1) { //Adding to the finalAmmount
                finalAmount = finalAmount + movementsList.get(i).amount;
            } else {    //Using the finalAmount
                finalAmount = movementsList.get(i).amount != 0 && !movementsList.get(i).currency.equals("")
                        ? movementsList.get(i).amount
                        : finalAmount + movementsList.get(i).amount;
            }
            String amountReg = "", finalAmountReg = "";

            //Formating the output
            if (movementsList.get(i).amount != 0 && !movementsList.get(i).currency.equals("")) {
                DecimalFormat df = new DecimalFormat("#.00");
                if (movementsList.get(i).currency.equals("$")) {
                    amountReg = "$" + df.format(movementsList.get(i).amount);
                    finalAmountReg = "$" + df.format(finalAmount);
                } else {
                    amountReg = df.format(movementsList.get(i).amount) + " " + movementsList.get(i).currency;
                    finalAmountReg = df.format(finalAmount) + " " + movementsList.get(i).currency;
                }
                String format = movementsList.get(i).amount < 0
                        ? ANSI_BLUE + "%-35s" + ANSI_RED + "%15s%15s \n" + ANSI_RESET
                        : ANSI_BLUE + "%-35s" + ANSI_RESET + "%15s%15s\n";
                if (i > 0)
                    System.out.printf("%40s" + format, " ", movementReg, amountReg, finalAmountReg);
                else
                    System.out.printf(format, movementReg, amountReg, finalAmountReg);
            } else {
                if (i == 0)
                    errorHandling("No amount in registry " + movementsList.get(i));
                DecimalFormat df = new DecimalFormat("#.00");
                double amount = finalAmount * -1;
                if (movementsList.get(0).currency.equals("$")) {
                    amountReg = "$" + df.format(amount);
                } else {
                    amountReg = df.format(amount) + " " + movementsList.get(0).currency;
                }
                String format = amount < 0
                        ? ANSI_BLUE + "%37s%-36s" + ANSI_RED + "%15s" + ANSI_RESET + "%15s \n" + ANSI_RESET
                        : ANSI_BLUE + "%37s%-36s" + ANSI_RESET + "%15s" + ANSI_RESET + "%15s\n";
                System.out.printf(format, " ", movementReg, amountReg, " 0");
            }
        }
    }

    /*
     * Funtion: getAmount
     * Parameter:
     *      File priceBD: To get the prices of the currencies
     * 
     * This function gets the amount of a registry
     */
    double getAmount(File priceDB) {
        double amount = 0;
        if (movementsList.size() > 2) { //Case: More than 2 movements on the same registry
            for (int i = 0; i < movementsList.size() - 1; i++) {    //Getting the amount of al the movements
                if (movementsList.get(i).currency.equals("$")) {
                    amount = amount + movementsList.get(i).amount;
                }
            }
            if (amount != 0)
                return amount; //Returning the final amount
        }
        for (int i = 0; i < movementsList.size(); i++) {    //With 2 movements, returning the amount
            if (movementsList.get(i).currency.equals("$")) {
                amount = movementsList.get(i).amount;
                return amount;
            }
        }
        try {   //Getting the final amount using the currencies from priceDB
            Scanner fileScanner = new Scanner(priceDB);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.indexOf(movementsList.get(0).currency) != -1) {
                    String[] priceSplit = line.split("\\s");
                    String price = priceSplit[priceSplit.length - 1].replace("$", "").trim();
                    amount = Double.parseDouble(price);
                    return amount; //Returning amoun
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) { //Catch if priceDB file not found
            e.printStackTrace();
            errorHandling("File " + priceDB.getName() + " not found\n" + e.getStackTrace());
        }
        errorHandling("Currency or amount not found in registry " + concept);   //Exit if currency not founded on priceDB
        return amount;
    }

    public Registry(List<String> registry) {
        rawRegistry = registry;
        movementsList = new ArrayList<Movement>();
        String[] firstLine = rawRegistry.get(0).split("\\s");
        String dateReg = firstLine[0];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd");    //Getting the date from the raw registry
        try {
            date = formatter.parse(dateReg);
        } catch (ParseException e) {    //Catching if incorrect date format
            errorHandling("Incorrect date format" + e.getStackTrace());
        }
        concept = registry.get(0).replace(dateReg + " ", "");
        for (int i = 1; i < rawRegistry.size(); i++) {
            String[] movements = rawRegistry.get(i).split("\\s");
            String currency = "";
            double amount = 0.0;
            String name = "";
            //Getting the amount, currency and name of the registry
            if (Pattern.compile("^\\$.*$").matcher(movements[movements.length - 1].trim()).matches()
                    || Pattern.compile("^-\\$.*$").matcher(movements[movements.length - 1].trim()).matches()) {
                amount = Double.parseDouble(movements[movements.length - 1].trim().replace("$", ""));
                currency = "$";
                name = rawRegistry.get(i).replace(movements[movements.length - 1], "").trim();
            } else if (Pattern.compile("^[0-9].*$").matcher(movements[movements.length - 2].trim()).matches()
                    || Pattern.compile("^-[0-9].*$").matcher(movements[movements.length - 2].trim()).matches()) {
                amount = Double.parseDouble(movements[movements.length - 2].trim());
                currency = movements[movements.length - 1].trim();
                name = rawRegistry.get(i)
                        .replace(movements[movements.length - 2] + " " + movements[movements.length - 1], "").trim();
            } else {
                name = rawRegistry.get(i);
            }
            Movement movement = new Movement(name, amount, currency); //Creating the movement
            movementsList.add(movement);    //Adding the movement to the list
        }
    }
}
