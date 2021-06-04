package ght.app.datalogger.data.logSystem;

/**
 * This Exception-Class handles the connections lost Exception
 * @author M.Gasser
 * @version 1.000  04.05.2021
 */
public class ExceptionConnectionLost extends Exception {

    //Constructor with the Messeage "Unit: xy, lost connection"
    public ExceptionConnectionLost(String message) {

        super(message);
        System.out.println("Unit: " + message  + ", lost connection");
    }
}
