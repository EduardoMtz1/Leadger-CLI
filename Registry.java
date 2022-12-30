import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Registry {
    public List<String> rawRegistry;
    Date date;
    String concept;
    List<Movement> movementsList;

    void errorHandling(String s) {
        System.out.println(s);
        System.exit(-1);
    }

    void plainPrint(){
        for(int i = 0; i < rawRegistry.size(); i++) System.out.println(rawRegistry.get(i));
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
