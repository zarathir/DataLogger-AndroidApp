package ght.app.datalogger.data.logSystem;

import android.content.Context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * This Class holds all the added Units and gives methodes to add/ remove a certain Unit.
 * Also it reads/writes the holded Units into a Project file if asked for.
 * @author M.Gasser
 * @version 1.000  04.05.2021
 */
public class UnitHandler implements Serializable{
    private static String pathToPackage = "DataLogger/app/src/main/java/ght/app/files/";
    private static File file = new File(pathToPackage + "MyUnits.ser");
    private static final String FILE_NAME = "MyUnits.ser";


    private ArrayList<LoggingUnit> myUnits;


    //Constructor
    public UnitHandler() {
        this.myUnits = new ArrayList<>();
    }


    /**
     * Methode to add a new Unit to the Unit-ArrayList.
     * It will add it just if not Unit with the same connection params is in the Unit-ArrayList.
     * It gives the feedback if it got fullfilled or not
     * @param  newUnit (as LoggingUnit)
     * @return  fullfilled (as boolean)
     */
    public boolean addUnit(LoggingUnit newUnit) throws Exception{
        if (newUnit != null){
            if (!checkSameUnit(newUnit)) {
                this.myUnits.add(newUnit);
                return true;
            } else {
                throw new Exception();
            }
        }
        return false;
    }

    /**
     * Methode to remove a Unit of to the Unit-ArrayList.
     * It gives the feedback if it got fullfilled or not
     * @param  choosenUnit (as LoggingUnit)
     * @return  fullfilled (as boolean)
     */
    public boolean removeUnit(LoggingUnit choosenUnit) {
        if (choosenUnit != null && this.myUnits.contains(choosenUnit)) {
            this.myUnits.remove(choosenUnit);
            return true;
        }
        return false;
    }

    /**
     * Methode to give out the whole Unit-ArrayList.
     */
    public ArrayList<LoggingUnit> getUnitArrayList() {
        return myUnits;
    }

    /**
     * Methode writes all the Units of the Unit-ArrayList into a file, to be able to restore it sometime later.
     */
    public void writeUnitsIntoFile(Context context) {

        if (myUnits.size() > 0) {

            try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                oos.writeObject(myUnits);
                PrintOnMonitor.printlnMon("------------------------------------------", PrintOnMonitor.Reason.GENERAL);
                PrintOnMonitor.printlnMon("Following Objects got written into file:", PrintOnMonitor.Reason.GENERAL);
                for (LoggingUnit oneUnit : myUnits) {
                    PrintOnMonitor.printlnMon(oneUnit.getUnitName(), PrintOnMonitor.Reason.GENERAL);
                }
                PrintOnMonitor.printlnMon("------------------------------------------", PrintOnMonitor.Reason.GENERAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Methode reads all the Units out of the file and restores the Unit-ArrayList with it.
     */
    public void readUnitsOfFile(Context context) throws ExceptionDeserialization {

        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            myUnits.clear();

            try {
                myUnits = (ArrayList<LoggingUnit>) ois.readObject();
            } catch (Exception ed) {
                throw new ExceptionDeserialization();
            }

            PrintOnMonitor.printlnMon("------------------------------------------", PrintOnMonitor.Reason.GENERAL);
            PrintOnMonitor.printlnMon("Following Objects got read out of the file:", PrintOnMonitor.Reason.GENERAL);
            for (LoggingUnit oneUnit : myUnits) {
                PrintOnMonitor.printlnMon(oneUnit.getUnitName(), PrintOnMonitor.Reason.GENERAL);
                oneUnit.initUnit();
            }
            PrintOnMonitor.printlnMon("------------------------------------------", PrintOnMonitor.Reason.GENERAL);
        } catch (FileNotFoundException ed) {
            throw new ExceptionDeserialization();
        } catch (StreamCorruptedException ed) {
            throw new ExceptionDeserialization();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Methode gives back the requested LoggingUnit-Object out of the Unit-ArrayList
     * @param unitname (as String)
     * @return null if it was not found / Unit-Object (as LoggingUnit) if it was found
     */
    public LoggingUnit getCertainUnit(String unitname) {

        for (LoggingUnit oneUnit : myUnits) {
            if (oneUnit.getUnitName().equals(unitname)) {
                return oneUnit;
            }
        }
        return null;
    }

    /*
     * Methode checks if a requested Unit with the same connection params is already stored in the Unit-ArrayList
     * @param unit (as LoggingUnit)
     * @return false if no unit has same connection params / true if any unit has same connection params (as LoggingUnit)
     */
    private boolean checkSameUnit(LoggingUnit unit) {

        for (LoggingUnit oneUnit : myUnits) {
            switch (oneUnit.getConnectionTyp()) {
                case WIFI:
                    if (oneUnit.getIpAdress().equals(unit.getIpAdress())) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

}
