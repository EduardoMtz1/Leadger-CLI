/*
 * Account.java
 */

import java.util.ArrayList;
import java.util.List;

public class Account {
    String name;    //name: Name of the account
    List<Movement> movements = new ArrayList<Movement>();   //movements: List of movements of the account
    List<Counting> countings = new ArrayList<Counting>();   //countings: List of amount separated by currency
    List<String> currencies = new ArrayList<String>();      //currencies: List of currencies used in the account

    public Account(String n){
        name = n;
    }

    /*
     * Funtion: getAmounts
     * 
     * This function gets the amount of the account separated by currencies used
     */

    void getAmounts(){
        for(int i = 0; i < movements.size(); i++){
            if(!currencies.contains(movements.get(i).currency)) { //Separating movements by currency
                currencies.add(movements.get(i).currency);
                Counting cCur = new Counting(movements.get(i).currency); //Creating counting to get the final ammount
                for(int j = 0; j < movements.size(); j++){
                    if(movements.get(j).currency.equals(cCur.currency)) cCur.movements.add(movements.get(j));
                }
                countings.add(cCur);
            }
        }
    }
}

