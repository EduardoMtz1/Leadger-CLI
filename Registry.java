import java.util.Date;
import java.util.List;

public class Registry {
    public List<String> rawRegistry;
    Date date;
    String concept, from, fromType, to, toType;
    public Registry(List<String> registry){
        rawRegistry = registry;
        for(int i = 0; i < rawRegistry.size(); i++){
            System.out.println(rawRegistry.get(i));
            String[] line = rawRegistry.get(i).split("\\s");
        }
    }
}
