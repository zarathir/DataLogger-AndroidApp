package ght.app.datalogger.data.units;

import ght.app.datalogger.data.logSystem.EnumConnection;
import ght.app.datalogger.data.logSystem.IntfWifi;
import ght.app.datalogger.data.logSystem.LoggingUnit;

import java.io.Serializable;
import java.net.InetAddress;


/**
 * This Class ist the Subclass of LoggingUnit and is for the Raspberry Unit,
 * it handles the connection / comands to the Unit / feedback, errors and the datestream from the Unit
 * @author M.Gasser
 * @version 1.000  04.05.2021
 */
public class UnitRaspberry extends LoggingUnit implements IntfWifi, Serializable {
    private static final long serialVersionID = 1L;

    private InetAddress ipAdress;
    private EnumConnection connectionTyp;


    //constructor
    public UnitRaspberry(String unitName, InetAddress ipAdress, EnumConnection connectionTyp) {
        super(unitName);
        this.ipAdress = ipAdress;
        this.connectionTyp = connectionTyp;
    }


    /**
     * Methode to get the IP-Adress of the Unit
     * @return  IP-Adress (as InetAddress)
     */
    @Override
    public InetAddress getIpAdress() {
        return ipAdress;
    }

    /**
     * Methode to get the connection type of the Unit
     * @return  connectiontyp WIFI/BLUETOOTH/... (as EnumConnection)
     */
    @Override
    public EnumConnection getConnectionTyp() {
        return connectionTyp;
    }
}