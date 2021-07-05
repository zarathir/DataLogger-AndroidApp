package ght.app.datalogger.data.units;

import ght.app.datalogger.data.logSystem.EnumConnection;
import ght.app.datalogger.data.logSystem.PrintOnMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;


import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This Class is testing the Class UnitArduino.
 * @author M.Gasser
 * @version 1.000  18.06.2021
 */
public class UnitArduinoTest {
    private static boolean firstSetup = true;

    private UnitArduino unit1;
    private UnitArduino unit2;

    /**
     * Test-Methode preperation: Setup the Test with two different Arduino units
     */
    @BeforeEach
    public  void setUp() throws Exception {
        if (firstSetup) {
            PrintOnMonitor.printlnMon("*** Setup Unittest for UnitArduino ***", PrintOnMonitor.Reason.UNITTEST);
            firstSetup = false;
        }

        InetAddress ipAdress = Inet4Address.getByName("192.168.0.15");
        unit1 = new UnitArduino("TestUnit1", ipAdress, EnumConnection.WIFI);
        ipAdress = Inet4Address.getByName("192.168.1.16");
        unit2 = new UnitArduino("TestUnit2", ipAdress, EnumConnection.WIFI);
    }

    /**
     * Test-Methode to check if the getIpAdress() gives back the right IP-Adress
     */
    @Test
    public void test_getIpAdress() throws Exception {
        PrintOnMonitor.printlnMon("Testing methode getIpAdress()", PrintOnMonitor.Reason.UNITTEST);

        assertEquals(unit1.getIpAdress(), Inet4Address.getByName("192.168.0.15"));
        assertEquals(unit2.getIpAdress(), Inet4Address.getByName("192.168.1.16"));
    }

    /**
     * Test-Methode to check if the getConnectionTyp() gives back the right ConnectionEnum
     */
    @Test
    public void test_getConnectionTyp() throws Exception {
        PrintOnMonitor.printlnMon("Testing methode getConnectionTyp()", PrintOnMonitor.Reason.UNITTEST);

        assertEquals(unit1.getConnectionTyp(), EnumConnection.WIFI);
        assertEquals(unit2.getConnectionTyp(), EnumConnection.WIFI);
    }
}