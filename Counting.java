import java.util.ArrayList;
import java.util.List;

public class Counting {
    String currency;
    List<Movement> movements = new ArrayList<Movement>();
    double total;
    public Counting(String cur){
        currency = cur;
    }

    double getTotal(){
        total = 0;
        for(int i = 0; i < movements.size(); i++){
            total = total + movements.get(i).amount;
        }
        return total;
    }
}
