package ght.app.datalogger.data.logSystem;

import android.content.Context;

import ght.app.datalogger.data.units.UnitArduino;
import ght.app.datalogger.data.units.UnitServerArduino;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This Class is testing the Class LoggingUnit.
 * @author M.Gasser
 * @version 1.000  18.06.2021
 */
public class LoggingUnitTest implements IntfGuiListener {
    private static final int numberOfLogDataLine = 1000;
    private static boolean firstSetup = true;

    private UnitArduino unitArd1;
    private UnitArduino unitArd2;
    private UnitArduino unitRasp1;
    private ArrayList<LoggingUnit> testUnits;

    int connectionStat;
    int connectionLost;
    int commandFeedback;
    int error;
    String unitName;

    //Constructor
    public LoggingUnitTest() {
        this.unitArd1 = null;
        this.unitArd2 = null;
        this.unitRasp1 = null;
        this.testUnits = new ArrayList<>();
        resetListenerTags();
    }

    /**
     * Methode to reset the Listener instance variable
     */
    private void resetListenerTags() {
        this.connectionStat = -1;
        this.connectionLost = -1;
        this.commandFeedback = -1;
        this.error = -1;
        this.unitName = null;
    }


    /**
     * Test-Methode preperation: Setup the Test with serveral different units
     * and put it into an ArrayList, to better check all of them
     */
    @BeforeEach
    public  void setUp() throws Exception {
        if (firstSetup) {
            PrintOnMonitor.printlnMon("*** Setup Unittest for LoggingUnit ***", PrintOnMonitor.Reason.UNITTEST);
            firstSetup = false;
        }

        InetAddress ipAdress = Inet4Address.getByName("127.0.0.1");
        unitArd1 = new UnitArduino("ArduinoUnit1", ipAdress, EnumConnection.WIFI);
        ipAdress = Inet4Address.getByName("192.168.0.16");
        unitArd2 = new UnitArduino("ArduinoUnit2", ipAdress, EnumConnection.WIFI);
        ipAdress = Inet4Address.getByName("192.168.2.17");
        unitRasp1 = new UnitArduino("RaspberryUnit1", ipAdress, EnumConnection.WIFI);

        testUnits.add(unitArd1);
        testUnits.add(unitRasp1);
        testUnits.add(unitArd2);

        connectionStat = -1;
        connectionLost = -1;
        commandFeedback = -1;
        error = -1;
        unitName = null;
    }


    /**
     * Test-Methode to check if the getUnitName() gives back the right unit name
     * of all of the Units
     */
    @Test
    public void test_getUnitName() {
        PrintOnMonitor.printlnMon("Testing methode getUnitName()", PrintOnMonitor.Reason.UNITTEST);

        assertEquals(unitArd1.getUnitName(), "ArduinoUnit1");
        assertEquals(unitArd2.getUnitName(), "ArduinoUnit2");
        assertEquals(unitRasp1.getUnitName(), "RaspberryUnit1");
    }

    /**
     * Test-Methode to check if the isConnected() gives back
     * the connectionstate that has been set.
     *
     */
    @Test
    public void test_isConnected() {
        PrintOnMonitor.printlnMon("Testing methode isConnected()", PrintOnMonitor.Reason.UNITTEST);

        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.setConnection(false);
        }
        assertFalse(unitArd1.isConnected());
        assertFalse(unitArd2.isConnected());
        assertFalse(unitRasp1.isConnected());
        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.setConnection(true);
        }
        assertTrue(unitArd1.isConnected());
        assertTrue(unitArd2.isConnected());
        assertTrue(unitRasp1.isConnected());

        //restore inital connectionstate
        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.setConnection(false);
        }
    }

    /**
     * Test-Methode is checking if the setConnection() changes the connection state
     * and if the i get informed by the Listeber CONNECTION_STATE.
     */
    @Test
    public void test_setConnection() throws Exception {
        PrintOnMonitor.printlnMon("Testing methode setConnection()", PrintOnMonitor.Reason.UNITTEST);

        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.addListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_STATE);
            oneUnit.setConnection(false);
            Thread.currentThread().sleep(100);
            assertEquals(unitName, oneUnit.getUnitName()); //Check if we check the right Listener answer
            assertEquals(connectionStat, 0); //Check if Listener set the connectionState
            assertFalse(oneUnit.isConnected());

            oneUnit.setConnection(true);
            Thread.currentThread().sleep(100);
            assertEquals(unitName, oneUnit.getUnitName()); //Check if we check the right Listener answer
            assertEquals(connectionStat, 1); //Check if Listener set the connectionState
            assertTrue(oneUnit.isConnected());

            Thread.currentThread().sleep(100);

            oneUnit.removeListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_STATE);
            resetListenerTags();
        }

        //restore inital connectionstate
        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.setConnection(false);
            oneUnit.removeListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_STATE);
        }

    }

    /**
     * Test-Methode is checking the methodes getLogDataList(), clearLogDataList(), addLogLine()
     * that are interacting with the ArrayList that will contain the Loggingdatas.
     */
    @Test
    public void test_HandleLogDataArrayList() throws Exception {
        PrintOnMonitor.printlnMon("Testing HandleLogDataArrayList", PrintOnMonitor.Reason.UNITTEST);

        for (LoggingUnit oneUnit : testUnits) {
            //check clearLogDataList() + getSizeLogDataList()
            oneUnit.clearLogDataList();
            assertEquals(oneUnit.getSizeLogDataList(), 0);

            //check addLogLine() with a text and Without a text
            oneUnit.addLogLine("");
            oneUnit.addLogLine(null);
            for (int i = 1; i < numberOfLogDataLine; i++) {
                oneUnit.addLogLine("TestLog Line" + (i+1));
            }
            assertEquals(oneUnit.getSizeLogDataList(), numberOfLogDataLine+1);

            //check getLogDataList() with many entries (numberOfLogDataLine)
            ArrayList<String> logLines = oneUnit.getLogDataList();
            assertEquals(logLines.get(0), "");
            assertEquals(logLines.get(1), null);
            assertEquals(logLines.get(2), "TestLog Line2");
            assertEquals(logLines.get(numberOfLogDataLine-1), "TestLog Line"+ (numberOfLogDataLine-1));
        }


        //restore inital connectionstate
        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.clearLogDataList();
        }
    }

    /**
     * Test-Methode is checking the methodes setStartTime(), getStartTime()
     * that are that are used to check a connection lost timeout.
     * It will check if the starttime gets set. After 100ms reset the starttime
     * and check if it raised its value for more then 100ms.
     */
    @Test
    public void test_TimeoutTimer() throws Exception {
        PrintOnMonitor.printlnMon("Testing TimoutTimer", PrintOnMonitor.Reason.UNITTEST);

        for (LoggingUnit oneUnit : testUnits) {
            //check if time is bigger then zero
            oneUnit.setStartTime();
            assertTrue(oneUnit.getStartTime() > 0);

            //reset starttime and check if the timestamp has been rised for more then 100ms
            long lastStarTime = oneUnit.getStartTime();
            Thread.currentThread().sleep(100);
            oneUnit.setStartTime();
            assertTrue(oneUnit.getStartTime() > lastStarTime + 100000);
        }
    }

    /**
     * Test-Methode is checking the methode connect()
     */
    @Test
    public void test_connect() throws Exception {
        PrintOnMonitor.printlnMon("Testing methode connect()", PrintOnMonitor.Reason.UNITTEST);

        //check if the ConnectException gets thrown if no connection could have been established
        assertThrows(ConnectException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                unitArd1.connect();
            }
        });

        //check if any connection can get established
        UnitServerArduino server = new UnitServerArduino();
        Thread serverThread = new Thread(server);
        serverThread.setName("TestServerThread");
        serverThread.start();

        unitArd1.connect();
        Thread.currentThread().sleep(200);
        assertTrue(unitArd1.isConnected());
        serverThread.interrupt();
        Thread.currentThread().sleep(200);
    }

    /**
     * Test-Methode is checking the methode sendCommand()
     */
    @Test
    public void test_sendCommand() throws Exception {
        PrintOnMonitor.printlnMon("Testing methode sendCommand()", PrintOnMonitor.Reason.UNITTEST);

        //prepare the Server
        UnitServerArduino server = new UnitServerArduino();
        Thread serverThread = new Thread(server);
        serverThread.setName("TestServerThread");
        serverThread.start();
        unitArd1.connect();

        //check if different comandos can be sent to the server without any exception
        Context myContext = null;
        assertTrue(unitArd1.sendCommand(-1, myContext));
        assertTrue(unitArd1.sendCommand(0, myContext));
        assertTrue(unitArd1.sendCommand(2147483647, myContext));
        assertTrue(unitArd1.sendCommand(-2147483648, myContext));
        serverThread.interrupt();
        Thread.currentThread().sleep(200);
    }

    /**
     * Test-Methode is checking the methode disconnect()
     */
    @Test
    public void test_disconnect() throws Exception {
        PrintOnMonitor.printlnMon("Testing methode disconnect()", PrintOnMonitor.Reason.UNITTEST);

        //check the disconnection if no SocketServer has been assigned befor that
        unitArd1.disconnect();
        Thread.currentThread().sleep(200);
        assertFalse(unitArd1.isConnected());

        //prepare the Server
        UnitServerArduino server = new UnitServerArduino();
        Thread serverThread = new Thread(server);
        serverThread.setName("TestServerThread");
        serverThread.start();
        unitArd1.connect();

        //check the disconnection if client is connected
        assertTrue(unitArd1.isConnected());
        unitArd1.disconnect();
        Thread.currentThread().sleep(200);
        assertFalse(unitArd1.isConnected());

        //check the disconnection if client is already disconnected
        unitArd1.disconnect();
        Thread.currentThread().sleep(200);
        assertFalse(unitArd1.isConnected());

        serverThread.interrupt();
        Thread.currentThread().sleep(200);
    }

    /**
     * Test-Methode is checking the methode getConnectionInterfaces()
     */
    @Test
    public void test_getConnectionInterfaces() {
        PrintOnMonitor.printlnMon("Testing methode getConnectionInterfaces()", PrintOnMonitor.Reason.UNITTEST);

        //check the discovered connection interfaces of UnitArduino-class
        ArrayList<String> unitInterfaces = LoggingUnit.getConnectionInterfaces(EnumUnits.ARDUINO);
        assertEquals(unitInterfaces.size(),1);
        assertEquals(unitInterfaces.get(0),"Wifi");
        unitInterfaces.clear();

        //check the discovered connection interfaces of UnitRaspberry-class
        unitInterfaces = LoggingUnit.getConnectionInterfaces(EnumUnits.RASPBERRY);
        assertEquals(unitInterfaces.size(),1);
        assertEquals(unitInterfaces.get(0),"Wifi");
        unitInterfaces.clear();

        //check the discovered connection interfaces if null is given as parameter
        unitInterfaces = LoggingUnit.getConnectionInterfaces(null);
        assertEquals(unitInterfaces.size(),0);
        unitInterfaces.clear();
    }

    /**
     * Test-Methode is checking the methodes that handles the GuiListeners
     */
    @Test
    public void test_GuiListener() {
        PrintOnMonitor.printlnMon("Testing methodes GuiListener", PrintOnMonitor.Reason.UNITTEST);

        //Check if add/notify/remove Listener with null-Parameters will throw an exception
        unitArd1.addListener(null, null);
        unitArd1.notifyListener(null, 7);
        unitArd1.removeListener(null, null);

        resetListenerTags();

        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.addListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_STATE);
            oneUnit.addListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_LOST);
            oneUnit.addListener((IntfGuiListener) this, LogUnitEvent.CMDFEEDBACK_RECEIVED);
            oneUnit.addListener((IntfGuiListener) this, LogUnitEvent.ERROR_RECEIVED);

            //Check CONNECTION_STATE listener
            oneUnit.notifyListener(LogUnitEvent.CONNECTION_STATE, -1);
            assertEquals(unitName, oneUnit.getUnitName());
            assertEquals(connectionStat, -1);
            resetListenerTags();

            //Check CONNECTION_LOST listener
            oneUnit.notifyListener(LogUnitEvent.CONNECTION_LOST, 0);
            assertEquals(unitName, oneUnit.getUnitName());
            assertEquals(connectionLost, 0);
            resetListenerTags();

            //Check CMDFEEDBACK_RECEIVED listener
            oneUnit.notifyListener(LogUnitEvent.CMDFEEDBACK_RECEIVED, 2147483647);
            assertEquals(unitName, oneUnit.getUnitName());
            assertEquals(commandFeedback, 2147483647);
            resetListenerTags();

            //Check ERROR_RECEIVED listener
            oneUnit.notifyListener(LogUnitEvent.ERROR_RECEIVED, -2147483648);
            assertEquals(unitName, oneUnit.getUnitName());
            assertEquals(error, -2147483648);
            resetListenerTags();

            //restore inital connectionstate
            oneUnit.removeListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_STATE);
            oneUnit.removeListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_LOST);
            oneUnit.removeListener((IntfGuiListener) this, LogUnitEvent.CMDFEEDBACK_RECEIVED);
            oneUnit.removeListener((IntfGuiListener) this, LogUnitEvent.ERROR_RECEIVED);
            resetListenerTags();
        }





        for (LoggingUnit oneUnit : testUnits) {
            oneUnit.removeListener((IntfGuiListener) this, LogUnitEvent.CONNECTION_STATE);
        }
    }


    //-------------------------------

    /**
     * Overwritten Methode of Interface IntfGuiListener: this is the Action that will be run, if one of the Listeners:
     * (connectionStateListener/connectionLostListener/comandReceivedListeners/errorReceivedListeners) gets notified,
     * @param lue (Loggingunit event) event-enum of the Event that shall be given to the Listeners (as LogUnitEvent);
     * @param value the boolean value itsself (as int);
     * @param unitName of the Unit that sent the Event (as String);
     */
    @Override
    public void loggingUnitEvent(LogUnitEvent lue, int value, String unitName) {
        switch (lue) {
            case CONNECTION_STATE:
                this.connectionStat = value;
                this.unitName = unitName;
                break;
            case CONNECTION_LOST:
                this.connectionLost = value;
                this.unitName = unitName;
                break;
            case CMDFEEDBACK_RECEIVED:
                this.commandFeedback = value;
                this.unitName = unitName;
                break;
            case ERROR_RECEIVED:
                this.error = value;
                this.unitName = unitName;
                break;
        }
    }
}
