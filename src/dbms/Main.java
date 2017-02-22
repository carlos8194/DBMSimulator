package dbms;

import interfaces.Interface;
import java.util.List;

/**
 * Created by Carlos on 18/02/2017.
 */
public class Main {

    public static void main(String[] args){
        DBMS dbms = new DBMS(1500,15,3,2,1,15);
        dbms.runSimultation();
    }
}
