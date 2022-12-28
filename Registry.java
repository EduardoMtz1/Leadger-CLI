import java.util.Date;
import java.util.List;

public class Registry {
    public List<String> rawRegistry;
    Date date;
    String concept, from, fromType, to, toType;
    public Registry(List<String> registry){
        rawRegistry = registry;
        for(int i = 0; i < rawRegistry.size(); i++){
            String[] line = rawRegistry.get(i).split("\\s");
            for(int j = 0; j < line.length; j++){
                
            }
        }
    }
}
