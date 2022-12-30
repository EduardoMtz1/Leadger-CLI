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
        } else if(accounts.contains(arg)){
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
        }else{
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
            for(int i = 0; i < registriesToUse.size(); i++){
                for(int j = 0; j < registriesToUse.get(i).movementsList.size(); j++){
                    if(registriesToUse.get(i).movementsList.get(j).name.indexOf(arg) != -1) finalList.add(registriesToUse.get(i));
                }
            }
            registriesToUse = finalList;
        }
        return registriesToUse;
    }

    void registerFunction() {

    }

    void balanceFunction() {

    }

    void printFunction(boolean sort, String sortType, List<String> actionArgs) {
        if(actionArgs.size() == 0){
            List<Registry> registries = getRegistries("");
            if(sort){
                if(sortType.toUpperCase().equals("DATE") || sortType.toUpperCase().equals("D")){
                    Collections.sort(registries, (a,b)->{
                        return a.date.compareTo(b.date);
                    });
                }
                if(sortType.toUpperCase().equals("AMOUNT") || sortType.toUpperCase().equals("A")){
                    Collections.sort(registries,(a,b)->{
                        return (int) (a.getAmount(priceDBFile) - b.getAmount(priceDBFile));
                    });
                }
            }
            for(int i = 0; i < registries.size(); i++){
                registries.get(i).plainPrint();
            }
        }else{
            for(int i = 0; i < actionArgs.size(); i++){
                List<Registry> registries = getRegistries(actionArgs.get(i));
                System.out.println(actionArgs.get(i));
                if(sort){
                    if(sortType.toUpperCase().equals("DATE") || sortType.toUpperCase().equals("D")){
                        Collections.sort(registries, (a,b)->{
                            return a.date.compareTo(b.date);
                        });
                    }
                    if(sortType.toUpperCase().equals("AMOUNT") || sortType.toUpperCase().equals("A")){
                        Collections.sort(registries,(a,b)->{
                            return (int) (a.getAmount(priceDBFile) - b.getAmount(priceDBFile));
                        });
                    }
                }
                for(int j = 0; j < registries.size(); j++){
                    registries.get(j).plainPrint();
                }
                
            }
        }
        
        
        
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
        // System.out.println(ledger.routeName);
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
            ledger.balanceFunction();
        if (action.equals("reg"))
            ledger.registerFunction();
        if (action.equals("print"))
            ledger.printFunction(sort, sortBy, actionArgs);
    }
}