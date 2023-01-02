import java.util.ArrayList;
import java.util.List;

public class Account {
    String name;
    List<Movement> movements = new ArrayList<Movement>();
    List<Counting> countings = new ArrayList<Counting>();
    List<String> currencies = new ArrayList<String>();

    public Account(String n){
        name = n;
    }

    void getAmounts(){
        for(int i = 0; i < movements.size(); i++){
            if(!currencies.contains(movements.get(i).currency)) {
                currencies.add(movements.get(i).currency);
                Counting cCur = new Counting(movements.get(i).currency);
                for(int j = 0; j < movements.size(); j++){
                    if(movements.get(j).currency.equals(cCur.currency)) cCur.movements.add(movements.get(j));
                }
                countings.add(cCur);
            }
        }
    }
}

