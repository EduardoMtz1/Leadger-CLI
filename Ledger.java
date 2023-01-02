import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Ledger {

    public File indexFile;
    public String routeName = "";
    public File priceDBFile;
    public List<String> accounts = new ArrayList<String>();
    public String sufix;

    void errorHandling(String s) {
        System.out.println(s);
        System.exit(-1);
    }

    List<Registry> getRegistries(String arg) {
        List<Registry> registriesToUse;
        if (arg.equals("")) {
            registriesToUse = new ArrayList<Registry>();
            for (int i = 0; i < accounts.size(); i++) {
                String routeAc = routeName + "/" + accounts.get(i) + "." + sufix;
                File account = new File(routeAc);
                try {
                    Scanner fileScanner = new Scanner(account);
                    List<String> rawRegistry;
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        if (line.charAt(0) == ';')
                            line = fileScanner.nextLine();
                        while (fileScanner.hasNextLine()) {
                            if (Pattern.compile("^[0-9].*$").matcher(line).matches()) {
                                rawRegistry = new ArrayList<String>();
                                rawRegistry.add(line);
                                boolean isDate = false;
                                while (fileScanner.hasNextLine() && !isDate) {
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
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    errorHandling("File " + accounts.get(i) + " not found\n" + e.getStackTrace());
                }
            }
        } else if (accounts.contains(arg)) {
            registriesToUse = new ArrayList<Registry>();
            String routeAc = routeName + "" + arg + "." + sufix;
            File account = new File(routeAc);
            try {
                Scanner fileScanner = new Scanner(account);
                List<String> rawRegistry;
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.charAt(0) == ';')
                        line = fileScanner.nextLine();
                    while (fileScanner.hasNextLine()) {
                        if (Pattern.compile("^[0-9].*$").matcher(line).matches()) {
                            rawRegistry = new ArrayList<String>();
                            rawRegistry.add(line);
                            boolean isDate = false;
                            while (fileScanner.hasNextLine() && !isDate) {
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                errorHandling("File " + arg + " not found\n" + e.getStackTrace());
            }
        } else {
            registriesToUse = new ArrayList<Registry>();
            for (int i = 0; i < accounts.size(); i++) {
                String routeAc = routeName + "/" + accounts.get(i) + "." + sufix;
                File account = new File(routeAc);
                try {
                    Scanner fileScanner = new Scanner(account);
                    List<String> rawRegistry;
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        if (line.charAt(0) == ';')
                            line = fileScanner.nextLine();
                        while (fileScanner.hasNextLine()) {
                            if (Pattern.compile("^[0-9].*$").matcher(line).matches()) {
                                rawRegistry = new ArrayList<String>();
                                rawRegistry.add(line);
                                boolean isDate = false;
                                while (fileScanner.hasNextLine() && !isDate) {
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
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    errorHandling("File " + accounts.get(i) + " not found\n" + e.getStackTrace());
                }
            }
            List<Registry> finalList = new ArrayList<Registry>();
            for (int i = 0; i < registriesToUse.size(); i++) {
                for (int j = 0; j < registriesToUse.get(i).movementsList.size(); j++) {
                    if (registriesToUse.get(i).movementsList.get(j).name.indexOf(arg) != -1)
                        finalList.add(registriesToUse.get(i));
                }
            }
            registriesToUse = finalList;
        }
        return registriesToUse;
    }

    void printableFunctions(boolean sort, String sortType, List<String> actionArgs, String functionToDo) {
        if (actionArgs.size() == 0) {
            List<Registry> registries = getRegistries("");
            if (sort) {
                if (sortType.toUpperCase().equals("DATE") || sortType.toUpperCase().equals("D")) {
                    Collections.sort(registries, (a, b) -> {
                        return a.date.compareTo(b.date);
                    });
                }
                if (sortType.toUpperCase().equals("AMOUNT") || sortType.toUpperCase().equals("A")) {
                    Collections.sort(registries, (a, b) -> {
                        return (int) (a.getAmount(priceDBFile) - b.getAmount(priceDBFile));
                    });
                }
            }
            for (int i = 0; i < registries.size(); i++) {
                if (functionToDo.equals("reg"))
                    registries.get(i).regPrint();
                else
                    registries.get(i).plainPrint();
            }
        } else {
            for (int i = 0; i < actionArgs.size(); i++) {
                List<Registry> registries = getRegistries(actionArgs.get(i));
                System.out.println(actionArgs.get(i));
                if (sort) {
                    if (sortType.toUpperCase().equals("DATE") || sortType.toUpperCase().equals("D")) {
                        Collections.sort(registries, (a, b) -> {
                            return a.date.compareTo(b.date);
                        });
                    }
                    if (sortType.toUpperCase().equals("AMOUNT") || sortType.toUpperCase().equals("A")) {
                        Collections.sort(registries, (a, b) -> {
                            return (int) (a.getAmount(priceDBFile) - b.getAmount(priceDBFile));
                        });
                    }
                }
                for (int j = 0; j < registries.size(); j++) {
                    if (functionToDo.equals("reg"))
                        registries.get(j).regPrint();
                    else
                        registries.get(j).plainPrint();
                }

            }
        }
    }

    List<Registry> getRegistriesBal(String arg) {
        List<Registry> registriesToUse = new ArrayList<Registry>();

        for (int i = 0; i < accounts.size(); i++) {
            String routeAc = routeName + "/" + accounts.get(i) + "." + sufix;
            File account = new File(routeAc);
            try {
                Scanner fileScanner = new Scanner(account);
                List<String> rawRegistry;
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.charAt(0) == ';')
                        line = fileScanner.nextLine();
                    while (fileScanner.hasNextLine()) {
                        if (Pattern.compile("^[0-9].*$").matcher(line).matches()) {
                            rawRegistry = new ArrayList<String>();
                            rawRegistry.add(line);
                            boolean isDate = false;
                            while (fileScanner.hasNextLine() && !isDate) {
                                line = fileScanner.nextLine();
                                if (!Pattern.compile("^[0-9].*$").matcher(line).matches()) {
                                    rawRegistry.add(line);
                                } else {
                                    isDate = true;
                                }
                            }
                            Registry reg = new Registry(rawRegistry);
                            boolean match = false;
                            for (int j = 0; j < reg.movementsList.size(); j++) {
                                if (reg.movementsList.get(j).name.indexOf(arg) != -1)
                                    match = true;
                            }
                            if (match)
                                registriesToUse.add(reg);
                        }
                    }
                }
                fileScanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                errorHandling("File " + accounts.get(i) + " not found\n" + e.getStackTrace());
            }
        }
        return registriesToUse;
    }

    void balanceFunction(List<String> actionArgs) {
        List<Movement> movements = new ArrayList<Movement>();
        if (actionArgs.size() == 0) {
            List<Registry> registries = getRegistries("");
            for (int j = 0; j < registries.size(); j++) {
                double finalAmount = 0;
                for (int k = 0; k < registries.get(j).movementsList.size(); k++) {
                    if (k < registries.get(j).movementsList.size() - 1) {
                        finalAmount = finalAmount + registries.get(j).movementsList.get(k).amount;
                    } else {
                        finalAmount = registries.get(j).movementsList.get(k).amount != 0
                                && !registries.get(j).movementsList.get(k).currency.equals("")
                                        ? registries.get(j).movementsList.get(k).amount
                                        : finalAmount + registries.get(j).movementsList.get(k).amount;
                    }
                    Movement regMov = registries.get(j).movementsList.get(k);
                    if (regMov.amount == 0 && regMov.currency.equals("")) {
                        regMov.amount = finalAmount * -1;
                        regMov.currency = registries.get(j).movementsList.get(0).currency;
                    }
                    movements.add(regMov);
                }
            }
        } else {
            for (int i = 0; i < actionArgs.size(); i++) {
                List<Registry> registries = getRegistriesBal(actionArgs.get(i));
                for (int j = 0; j < registries.size(); j++) {
                    double finalAmount = 0;
                    for (int k = 0; k < registries.get(j).movementsList.size(); k++) {
                        if (k < registries.get(j).movementsList.size() - 1) {
                            finalAmount = finalAmount + registries.get(j).movementsList.get(k).amount;
                        } else {
                            finalAmount = registries.get(j).movementsList.get(k).amount != 0
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
                            movements.add(regMov);
                        }
                    }
                }
            }
        }

        int maxLengthName = 0;
        for (int i = 0; i < movements.size(); i++) {
            String[] acc = movements.get(i).name.trim().split("\\:");
            if (acc.length > maxLengthName)
                maxLengthName = acc.length;
        }

        printBalance("", 0, maxLengthName, movements);

        List<Counting> countings = new ArrayList<Counting>();
        List<String> currencies = new ArrayList<String>();
        for (int i = 0; i < movements.size(); i++) {
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

        for(int i = 0; i < countings.size(); i++) System.out.println(countings.get(i).currency + countings.get(i).getTotal());

    }

    void printBalance(String lastName, int iterations, int maxLengthName, List<Movement> movements) {
        List<Account> accountsBalance = new ArrayList<Account>();
        List<String> accountsName = new ArrayList<String>();
        for (int i = 0; i < movements.size(); i++) {
            String[] acc = movements.get(i).name.trim().split("\\:");
            //System.out.println(movements.get(i).name + "-> " + acc.length);
            if (iterations < acc.length && !accountsName.contains(acc[iterations])) {
                accountsName.add(acc[iterations]);
                Account tempAcc = new Account(acc[iterations]);
                for (int j = 0; j < movements.size(); j++) {
                    String[] nAcc = movements.get(j).name.trim().split("\\:");
                    if (iterations < acc.length && iterations < nAcc.length && nAcc[iterations].equals(acc[iterations]))
                        tempAcc.movements.add(movements.get(j));
                }
                accountsBalance.add(tempAcc);
            }
        }

        for (int i = 0; i < accountsBalance.size(); i++) {
            if (accountsBalance.get(i).movements.size() > 1) {
                List<String> tempAccountsName = new ArrayList<String>();
                int movementsUsed = 0;
                for(int j = 0; j < accountsBalance.get(i).movements.size(); j++){
                    String[] acc = accountsBalance.get(i).movements.get(j).name.trim().split("\\:");
                    if(iterations + 1 < acc.length && !tempAccountsName.contains(acc[iterations + 1]))tempAccountsName.add(acc[iterations + 1]);
                    if(iterations + 1 < acc.length && tempAccountsName.contains(acc[iterations + 1])) movementsUsed++;
                }
                if(tempAccountsName.size() > 1){
                    System.out.println(lastName + accountsBalance.get(i).name + ":");
                    accountsBalance.get(i).getAmounts();
                    for (int j = 0; j < accountsBalance.get(i).countings.size(); j++) {
                        System.out.println(accountsBalance.get(i).countings.get(j).currency
                                + accountsBalance.get(i).countings.get(j).getTotal());
                    }
                    printBalance(lastName, iterations + 1, maxLengthName, accountsBalance.get(i).movements);
                }else if(tempAccountsName.size() == 1){
                    if(movementsUsed < accountsBalance.get(i).movements.size()){
                        System.out.println(lastName + accountsBalance.get(i).name);
                    accountsBalance.get(i).getAmounts();
                    for (int j = 0; j < accountsBalance.get(i).countings.size(); j++) {
                        System.out.println(accountsBalance.get(i).countings.get(j).currency
                                + accountsBalance.get(i).countings.get(j).getTotal());
                    }
                    printBalance("", iterations + 1, maxLengthName, accountsBalance.get(i).movements);

                    }else
                    printBalance(lastName + accountsBalance.get(i).name + ":", iterations + 1, maxLengthName, accountsBalance.get(i).movements);
                }else{
                    System.out.println(lastName + accountsBalance.get(i).name);
                    accountsBalance.get(i).getAmounts();
                    for (int j = 0; j < accountsBalance.get(i).countings.size(); j++) {
                        System.out.println(accountsBalance.get(i).countings.get(j).currency
                                + accountsBalance.get(i).countings.get(j).getTotal());
                    }
                }
            } else {
                String finalName = "";
                String[] nameParts = accountsBalance.get(i).movements.get(0).name.trim().split("\\:");
                for(int j = iterations; j <nameParts.length; j++) finalName = finalName + nameParts[j]+":";
                System.out.println(finalName);
                accountsBalance.get(i).getAmounts();
                for (int j = 0; j < accountsBalance.get(i).countings.size(); j++) {
                    System.out.println(accountsBalance.get(i).countings.get(j).currency
                            + accountsBalance.get(i).countings.get(j).getTotal());
                }
            }
        }
        /*
         * for(int i = 0; i < accountsBalance.size(); i++) {
         * accountsBalance.get(i).getAmounts();
         * System.out.println(accountsBalance.get(i).name);
         * for(int j = 0; j < accountsBalance.get(i).countings.size();j++){
         * System.out.println(accountsBalance.get(i).countings.get(j).currency +
         * accountsBalance.get(i).countings.get(j).getTotal());
         * }
         * for(int j = 0; j < accountsBalance.get(i).movements.size(); j++){
         * System.out.println(accountsBalance.get(i).movements.get(j).name.trim() +
         * accountsBalance.get(i).movements.get(j).currency +
         * accountsBalance.get(i).movements.get(j).amount);
         * }
         * accountsBalance.get(i).getAmounts();
         * System.out.println(accountsBalance.get(i).name);
         * for(int j = 0; j < accountsBalance.get(i).countings.size(); j++){
         * System.out.println(accountsBalance.get(i).countings.get(j).currency +
         * accountsBalance.get(i).countings.get(j).getTotal());
         * }
         * }
         * 
         * if(accountsBalance.size() > 1){
         * for(int i = 0; i < accountsBalance.size() ; i++){
         * List<Movement> newMovementsList = new ArrayList<Movement>();
         * for(int j = 0; j < accountsBalance.get(i).movements.size(); j++){
         * String[] nameAcc =
         * accountsBalance.get(i).movements.get(j).name.trim().split("\\:");
         * if(nameAcc.length > iterations +
         * 1)newMovementsList.add(accountsBalance.get(i).movements.get(j));
         * }
         * if(newMovementsList.size() > 0){
         * String[] nameAcc = newMovementsList.get(0).name.trim().split("\\:");
         * String finalName = "";
         * for(int j = iterations; j < nameAcc.length; j++) finalName = finalName +
         * nameAcc[j] + ":";
         * String finalAmount = newMovementsList.get(0).currency +
         * newMovementsList.get(0).amount;
         * if(newMovementsList.size() == 1) System.out.println(finalName + " " +
         * finalAmount);
         * else printBalance(lastName + accountsBalance.get(i).name + ":",iterations+1,
         * maxLengthName, newMovementsList);
         * }
         * }
         * 
         * 
         * }else{
         * System.out.println(lastName + accountsBalance.get(0).name);
         * accountsBalance.get(0).getAmounts();
         * for(int j = 0; j < accountsBalance.get(0).countings.size(); j++){
         * System.out.println(accountsBalance.get(0).countings.get(j).currency +
         * accountsBalance.get(0).countings.get(j).getTotal());
         * }
         * printBalance(lastName, iterations + 1, maxLengthName,
         * accountsBalance.get(0).movements);
         * }
         */
    }

    public static void main(String[] args) {
        String priceDB = "", index = "", sortBy = "", action = "";
        Boolean sort = false, actionReady = false;
        Ledger ledger = new Ledger();
        List<String> actionArgs = new ArrayList<String>();
        if (args.length == 0) {
            ledger.errorHandling("No arguments found");
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].trim().equals("--price-db")) {
                actionReady = false;
                if (i + 1 == args.length)
                    ledger.errorHandling("No price-db file defined");
                priceDB = args[i + 1].trim();
            }
            if (args[i].trim().equals("-f") || args[i].trim().equals("--file")) {
                actionReady = false;
                if (i + 1 == args.length)
                    ledger.errorHandling("No file defined");
                index = args[i + 1].trim();
            }
            if (args[i].trim().equals("-s") || args[i].trim().equals("--sort")) {
                actionReady = false;
                sort = true;
                if (i + 1 == args.length)
                    ledger.errorHandling("No sort mode defined");
                sortBy = args[i + 1].trim();
            }
            if (actionReady)
                actionArgs.add(args[i].trim());
            if (args[i].trim().equals("bal") || args[i].trim().equals("balance")) {
                action = "bal";
                actionReady = true;
            }
            if (args[i].trim().equals("reg") || args[i].trim().equals("registry")) {
                action = "reg";
                actionReady = true;
            }
            if (args[i].trim().equals("print")) {
                action = "print";
                actionReady = true;
            }

        }

        ledger.indexFile = new File(index);
        if (!ledger.indexFile.exists()) {
            ledger.errorHandling("File " + index + " not found");
        }
        ledger.routeName = index.replaceAll(ledger.indexFile.getName(), "");
        ledger.priceDBFile = new File(priceDB);
        if (!ledger.priceDBFile.exists()) {
            ledger.errorHandling("File " + priceDB + " not found");
        }

        try {
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
        } catch (FileNotFoundException e) {
            ledger.errorHandling("File " + index + " not found\n" + e.getStackTrace());
        }

        if (action.equals(""))
            ledger.errorHandling("No action defined");
        if (action.equals("bal"))
            ledger.balanceFunction(actionArgs);
        if (action.equals("reg") || action.equals("print"))
            ledger.printableFunctions(sort, sortBy, actionArgs, action);
    }
}