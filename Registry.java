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
    public List<String> rawRegistry;
    Date date;
    String concept;
    List<Movement> movementsList;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";

    void errorHandling(String s) {
        System.out.println(s);
        System.exit(-1);
    }

    void plainPrint(){
        for(int i = 0; i < rawRegistry.size(); i++) System.out.println(rawRegistry.get(i));
    }

    void regPrint(){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        double lastAmount = 0;
        Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dateReg = calendar.get(calendar.YEAR) + "-" + months[calendar.get(calendar.MONTH)] + "-" + calendar.get(calendar.DAY_OF_MONTH);
            String conceptReg = concept;
            if(conceptReg.length() > 27)conceptReg = conceptReg.substring(0, 25) + "..";
            System.out.printf("%-11s %-28s",dateReg,conceptReg);
            for(int i = 0; i < movementsList.size(); i++){
                String movementReg = movementsList.get(i).name;
                if(movementReg.length() > 35)movementReg = movementReg.substring(0, 33) + "..";
                String amountReg = "";
                if(movementsList.get(i).amount != 0 && !movementsList.get(i).currency.equals("")){
                    DecimalFormat df = new DecimalFormat("#.00");
                    if(movementsList.get(i).currency.equals("$")){
                        amountReg = "$" + df.format(movementsList.get(i).amount);
                    }else{
                        amountReg = df.format(movementsList.get(i).amount) + " " + movementsList.get(i).currency;
                    }
                    String format = movementsList.get(i).amount < 0 ?ANSI_BLUE + "%-35s" + ANSI_RED + "%15s%15s \n" +ANSI_RESET:ANSI_BLUE + "%-35s" +ANSI_RESET +"%15s%15s\n";
                    if(i > 0)
                    System.out.printf("%40s" + format, " ", movementReg, amountReg, amountReg);
                    else
                    System.out.printf(format, movementReg, amountReg, amountReg);
                }else{
                    if( i == 0) errorHandling("No amount in registry " + movementsList.get(i));
                    DecimalFormat df = new DecimalFormat("#.00");
                    double amount = movementsList.get(0).amount * -1;
                    if(movementsList.get(0).currency.equals("$")){
                        amountReg = "$" + df.format(amount);
                    }else{
                        amountReg = df.format(amount) + " " + movementsList.get(0).currency;
                    }
                    String format = amount < 0 ?ANSI_BLUE + "%37s%-36s" +  ANSI_RED + "%15s" + ANSI_RESET+"%15s \n" +ANSI_RESET:ANSI_BLUE + "%37s%-36s" +  ANSI_RESET +"%15s"+ ANSI_RESET+"%15s\n";
                    System.out.printf(format, " ",movementReg,amountReg, " 0");
                }
            }
    }

    double getAmount(File priceDB){
        double amount = 0;
        for(int i = 0; i < movementsList.size(); i++){
            if(movementsList.get(i).currency.equals("$")) {
                amount = movementsList.get(i).amount;
                return amount;
            }
        }
        try {
            Scanner fileScanner = new Scanner(priceDB);
            while(fileScanner.hasNextLine()){
                String line = fileScanner.nextLine();
                if(line.indexOf(movementsList.get(0).currency) != -1){
                    String[] priceSplit = line.split("\\s");
                    String price = priceSplit[priceSplit.length - 1].replace("$", "").trim();
                    amount = Double.parseDouble(price);
                    return amount;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorHandling("File " + priceDB.getName() + " not found\n" + e.getStackTrace());
        }
        errorHandling("Currency or amount not found in registry " + concept);
        return amount;
    }

    public Registry(List<String> registry){
        rawRegistry = registry;
        movementsList = new ArrayList<Movement>();
        String[] firstLine = rawRegistry.get(0).split("\\s");
        String dateReg = firstLine[0];
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy/mm/dd");  
        try {
            date = formatter.parse(dateReg);
        } catch (ParseException e) {
            errorHandling("Incorrect date format" + e.getStackTrace());
        }
        concept = registry.get(0).replace(dateReg + " ", "");
        for(int i = 1; i < rawRegistry.size(); i++){
            String[] movements = rawRegistry.get(i).split("\\s");
            String currency = "";
            double amount = 0.0;
            String name = "";
            if(Pattern.compile("^\\$.*$").matcher(movements[movements.length - 1].trim()).matches() 
                || Pattern.compile("^-\\$.*$").matcher(movements[movements.length - 1].trim()).matches()){
                amount = Double.parseDouble(movements[movements.length - 1].trim().replace("$", ""));
                currency = "$";
                name = rawRegistry.get(i).replace(movements[movements.length - 1], "").trim();
            }else if(Pattern.compile("^[0-9].*$").matcher(movements[movements.length - 2].trim()).matches() 
                || Pattern.compile("^-[0-9].*$").matcher(movements[movements.length - 2].trim()).matches()){
                amount = Double.parseDouble(movements[movements.length - 2].trim());
                currency = movements[movements.length - 1].trim();
                name = rawRegistry.get(i).replace(movements[movements.length - 2] + " " + movements[movements.length - 1], "").trim();
            }else{
                name = rawRegistry.get(i);
            }
            Movement movement = new Movement(name, amount, currency);
            movementsList.add(movement);
        }
    }
}
