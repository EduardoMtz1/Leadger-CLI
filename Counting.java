/*
 * Counting.java
 */

import java.util.ArrayList;
import java.util.List;

public class Counting {
    String currency;    //currency: Symbol of the currency used
    List<Movement> movements = new ArrayList<Movement>();   //movements: List of movements used to get the total amount
    double total;       //total: Total ammount
    public Counting(String cur){
        currency = cur;
    }

    /*
     * Funtion: getTotal
     * 
     * This function gets the total amount of a list of movements
     */

    double getTotal(){
        total = 0;
        for(int i = 0; i < movements.size(); i++){
            total = total + movements.get(i).amount;
        }
        return total;
    }
}
