package ght.app.datalogger.data.logSystem;

import java.net.InetAddress;

/**
 * This Interface shold be implemented into those EnumUnits that shall be able to connect with Wifi.
 * Warning: the Interfacename "IntfWifi" should be as following: "Intf" + connectiontype, and the connectiontype should be the same letters as in the EnumConnection.
 * @author M.Gasser
 * @version 1.000  05.05.2021
 */
public interface IntfWifi {

    /**
     * Methode to get the get the IP-Adress of the Unit
     * @return  IP-Adress (as InetAddress)
     */
    InetAddress getIpAdress();
}
