package ght.app.datalogger.data.logSystem;

/**
 * This Exception-Class handles the deserialization Exception
 * @author M.Gasser
 * @version 1.000  27.05.2021
 */
public class ExceptionDeserialization extends Exception {

    //Constructor without Messeage
    public ExceptionDeserialization() {

        System.out.println("General: deserialization of units not possible");
    }
}
