package ght.app.datalogger.data.logSystem;


import ght.app.datalogger.data.units.UnitArduino;
import ght.app.datalogger.data.units.UnitRaspberry;
import ght.app.datalogger.data.logSystem.IntfConnectionListener.ConnectionEvent;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This Class is the Superclass of all the differend Units,
 * it handles the connection / comands to the Unit / feedback, errors and the datestream from the Unit
 * @author M.Gasser
 * @version 1.000  04.05.2021
 */
public abstract class LoggingUnit implements Serializable {
    private static final long serialVersionID = 1L;
    private static final int serverPortNumber = 5001;
    private static final long connectionTimeoutDefault_ms = 2000; //timeout in normalmode: 2s
    private static final long connectionTimeoutDebug_ms = 30000; //timeout in debugmode: 30s

    private String unitName;
    private EnumUnits unitVendor;
    private transient boolean connected;
    private transient ArrayList<String> logDataList;

    private transient Socket clientSocket;
    private transient PrintWriter writer;
    private transient InputStreamReader streamReader;
    private transient BufferedReader reader;

    private transient Thread unitReaderThread;
    private transient Thread unitConnectionThread;

    private transient List<IntfConnectionListener> connectionStateListeners;
    private transient List<IntfConnectionListener> connectionLostListeners;
    private transient List<IntfConnectionListener> comandReceivedListeners;
    private transient List<IntfConnectionListener> errorReceivedListeners;

    private transient static String pathToPackage = "DataLoggerApp/src/main/java/ght/app/files/";
    private transient static File logfile = new File(pathToPackage + "datalog.txt");

    private transient long startTime;

    //Constructor
    public LoggingUnit(String unitName) {
        this.unitName = unitName;
        initUnit();
    }

    /**
     * Methode initalizes an LoggingUnit-Object (by calling the constructor as well as ....
     * restoring Units by calling readUnitsOfFile on the UnitHandler)
     */
    public void initUnit() {
        this.connected = false;
        this.logDataList = new ArrayList<>();
        this.clientSocket = null;
        this.writer = null;
        this.streamReader = null;
        this.reader = null;
        this.unitReaderThread = null;
        this.unitConnectionThread = null;
        if (this instanceof UnitArduino) {
            this.unitVendor = EnumUnits.ARDUINO;
        } else if (this instanceof UnitRaspberry) {
            this.unitVendor = EnumUnits.RASPBERRY;
        }

        this.connectionStateListeners = new ArrayList<>();
        this.connectionLostListeners = new ArrayList<>();
        this.comandReceivedListeners = new ArrayList<>();
        this.errorReceivedListeners = new ArrayList<>();

        this.startTime = 0;
    }


    /**
     * Methode to get the Unit name
     * @return  unitName (as String)
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * Methode to get the Connectionstate (True= connected / false= disconected)
     * @return  connected (as boolean)
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Methode to sets the Connectionstate into the instance variable "connected"
     * if it has done set also the connectionstate Property to the same value.
     * @param  connectionState (as boolean)
     */
    private void setConnection(boolean connectionState) {
        this.connected = connectionState;
        if (connectionState) {
            notifyListener(ConnectionEvent.CONNECTION_STATE, 1);
        }else {
            notifyListener(ConnectionEvent.CONNECTION_STATE, 0);
        }
    }

    /**
     * Methode adds an Element to the Arraylist logDataList
     * @param logLine (as String)
     */
    private void addLogLine(String logLine) {
        logDataList.add(logLine);
    }

    /**
     * Methode clears the whole Arraylist logDataList to make it ready to get new elements
     */
    private void clearLogDataList() {
        logDataList.clear();
    }

    /**
     * Methode gives out the whole ArrayList logDataList with all its Loggingdatas
     * @return logDataList (as ArrayList<String>)
     */
    public ArrayList<String> getLogDataList() {
        return logDataList;
    }

    /**
     * Methode to get the IP-Adress of the Unit
     * @return  IP-Adress (as InetAddress)
     */
    public abstract InetAddress getIpAdress();

    /**
     * Methode to get the connection type of the Unit
     * @return  connectiontyp WIFI/BLUETOOTH/... (as EnumConnection)
     */
    public abstract EnumConnection getConnectionTyp();

    /**
     * Methode gives out the Timestamp when the last Connectioncheck ping got received
     * @return  startTime (as long)
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Methode sets the Timestamp to check later how much time has past after the the last Connectioncheck ping got received
     */
    public void setStartTime() {
        this.startTime = System.nanoTime();
    }

    /**
     * Methode tries to build up a connection to the Unit
     * @return connectionstate true=connected / false=disconnected (as boolean)
     */
    /*public boolean connect() throws Exception{
        if (!isConnected()) {
            //Client Socket erzeugen, incl. Verbindungsanfrage
            try {
                //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", try to connect to Server at: " + getIpAdress().toString().substring(1), PrintOnMonitor.Reason.CONNECTION);
                //clientSocket = new Socket(getIpAdress().toString().substring(1), serverPortNumber);

                clientSocket = new Socket(InetAddress.getByName("192.168.0.104"), serverPortNumber);
                //clientSocket.setKeepAlive(false);

                writer = new PrintWriter(clientSocket.getOutputStream());

                streamReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(streamReader);

            } catch (ConnectException e) {
                //no Connection could have been established
                throw e;
            }

            if (!clientSocket.isClosed()) {
                //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", connection established!", PrintOnMonitor.Reason.CONNECTION);
                //start the unitReaderThread
                unitReaderThread = new Thread(new UnitReader());
                unitReaderThread.setName("ReaderThreadUnit_" + getUnitName());
                unitReaderThread.start();
            }

            try {
                Thread.currentThread().sleep(200);
            } catch (IllegalArgumentException ignored) {
                ;// to prevent Exception: "Not on FX application thread;"
            }

            //PrintOnMonitor.printlnMon("Unit: "+ getUnitName() + ", Connectionstate of connection to Server: " + isConnected(), PrintOnMonitor.Reason.CONNECTION);
        }
        return isConnected();
    }*/

    /**
     * Methode disconnects the connection to the Unit
     * @return connectionstate: true=connected / false=disconnected (as boolean)
     */
    /*public boolean disconnect() {
        if (isConnected()) {
            try{
                clientSocket.close();
            }catch(IOException ex){
                ex.printStackTrace();
            } finally {
                if (clientSocket.isClosed()) {
                    //PrintOnMonitor.printlnMon("Thread: " + unitReaderThread.getName() + ", gets interrupted, by disconnecting!", PrintOnMonitor.Reason.THREAD);
                    //interrupt the unitReaderThread
                    unitReaderThread.interrupt();
                    try {
                        unitReaderThread.join();
                        writer = null;
                        reader = null;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (!isConnected()) {
            //PrintOnMonitor.printlnMon("Unit: "+ getUnitName() + ", disconnected!", PrintOnMonitor.Reason.CONNECTION);
        } else {
            //PrintOnMonitor.printlnMon("Unit: "+ getUnitName() + ", Connectionstate of disconnection of Server: " + isConnected(), PrintOnMonitor.Reason.CONNECTION);
        }
        return isConnected();
    }*/

    /**
     * Methode sends a command number to the Unit
     * @param commandNo that has to be sent to the unit (as int)
     * @return writer state: true=all allright / false=Error occured or not unit connected (as boolean)
     */
    public boolean sendCommand(int commandNo) {
        if (isConnected()) {
            //write into Socket

            String command = "#" + commandNo;
            writer.println(command);
            //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", Commando sent to Server: " + command, PrintOnMonitor.Reason.UNITINTERFACE);

            boolean writerError = writer.checkError();
            if (writerError) {
                //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", Writer set an error: ", PrintOnMonitor.Reason.UNITINTERFACE);
            }

            return !writerError;
        }
        return false;
    }


    /**
     * Methode to get all the connection-interfaces of the requested vendor class
     * @param requestedVendor (as EnumUnits)
     * @return Arraylist with all the interfaces (as ArrayList<String>)
     */
    public static ArrayList<String> getConnectionInterfaces(EnumUnits requestedVendor) {
        ArrayList<String> myConnections = new ArrayList<>();

        Class<?>[] interfaces = null;
        switch (requestedVendor) {
            case ARDUINO:
                Class<UnitArduino> ardClass = UnitArduino.class;
                interfaces = ardClass.getInterfaces();
                break;
            case RASPBERRY:
                Class<UnitRaspberry> raspClass = UnitRaspberry.class;
                interfaces = raspClass.getInterfaces();
                break;
        }

        for (Class<?> oneInterface : interfaces) {
            if (oneInterface.toString().contains("Intf")) {
                int interfaceIndex = oneInterface.toString().indexOf("Intf");
                int stringlength = oneInterface.toString().length();
                String interfaceName = oneInterface.toString().substring(interfaceIndex+4,stringlength);
                myConnections.add(interfaceName);
            }
        }
        return myConnections;
    }

    /**
     * Methode to add a Listener to the List "connectionStateListener" or "connectionLostListener"
     * @param cl (connectionListener) of the Interface IntfConnectionListener (as IntfConnectionListener)
     */
    public void addListener(IntfConnectionListener cl, ConnectionEvent ce) {
        switch (ce) {
            case CONNECTION_STATE:
                connectionStateListeners.add(cl);
                break;
            case CONNECTION_LOST:
                connectionLostListeners.add(cl);
                break;
            case CMDFEEDBACK_RECEIVED:
                comandReceivedListeners.add(cl);
                break;
            case ERROR_RECEIVED:
                errorReceivedListeners.add(cl);
                break;
        }
    }

    /**
     * Methode to remove a Listener of the List "connectionStateListener" or "connectionLostListener", just if an Listener is already added
     * @param cl (connectionListener) of the Interface IntfConnectionListener (as IntfConnectionListener)
     */
    public void removeListener(IntfConnectionListener cl, ConnectionEvent ce) {
        switch (ce) {
            case CONNECTION_STATE:
                if (connectionStateListeners.size()>0) {
                    connectionStateListeners.remove(cl);
                }
                break;
            case CONNECTION_LOST:
                if (connectionLostListeners.size()>0) {
                    connectionLostListeners.remove(cl);
                }
                break;
            case CMDFEEDBACK_RECEIVED:
                if (comandReceivedListeners.size()>0) {
                    comandReceivedListeners.remove(cl);
                }
                break;
            case ERROR_RECEIVED:
                if (errorReceivedListeners.size()>0) {
                    errorReceivedListeners.remove(cl);
                }
                break;
        }
    }

    /**
     * Methode to notify all the Listeners of the List "connectionStateListener" or "connectionLostListener".
     * The Listeners will get the Event itsself (as Enum) the value that has been given and the UnitName (as String)
     * @param ce (enum-value) of the Event that shall be given to the Listeners (as ConnectionEvent); the boolean value itsself (as boolean)
     */
    private void notifyListener(ConnectionEvent ce, int value) {
        switch (ce) {
            case CONNECTION_STATE:
                connectionStateListeners.forEach((cl) -> {
                    //all connectionStateListeners gets the given Event.
                    cl.connectionEvent(ce, value, getUnitName());
                });
                break;
            case CONNECTION_LOST:
                connectionLostListeners.forEach((cl) -> {
                    //all connectionLostListeners gets the given Event.
                    cl.connectionEvent(ce, value, getUnitName());
                });
                break;
            case CMDFEEDBACK_RECEIVED:
                comandReceivedListeners.forEach((cl) -> {
                    //all connectionLostListeners gets the given Event.
                    cl.connectionEvent(ce, value, getUnitName());
                });
                break;
            case ERROR_RECEIVED:
                errorReceivedListeners.forEach((cl) -> {
                    //all connectionLostListeners gets the given Event.
                    cl.connectionEvent(ce, value, getUnitName());
                });
                break;
        }
    }

    public void connect() {
        //start the unitConnectionThread
        unitConnectionThread = new Thread(new Connection());
        unitConnectionThread.setName("ConnectionThreadUnit_" + getUnitName());
        unitConnectionThread.start();
    }

    public void disconnect() {
        //interrupt the unitConnectionThread
        unitConnectionThread.interrupt();

    }

    //------------------------------------------------------------------------------------------------------------------

    /*
     * This inner Class is the Reader of this certain Unit. It has implemented a reader to get all the different datas of a Unit (comand-feedbacks / errors / datas) as requested.
     * It will be started as an own Thread as soon as a connection gets established to this Unit.
     * It will get interrupted as soon as a disconnection is perfomed or if the Unit looses its connection.
     * @author M.Gasser
     * @version 1.000  09.06.2021
     */
    private class UnitReader implements Runnable {


        //Constructor
        public UnitReader() {
            setStartTime();
        }



        @Override
        public void run() {
            //PrintOnMonitor.printlnMon("Thread: " + Thread.currentThread().getName() + ", is running!", PrintOnMonitor.Reason.THREAD);
            setConnection(!clientSocket.isClosed());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (reader.ready()) {
                        readProtocol();
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
/*
                //check if timeout of connectioncheck ping has passt since the last ping of server (longer timeout while debugging)
                boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                        getInputArguments().toString().indexOf("jdwp") >= 0;
*/
                //set timeout for checking connection (debugmode= 30s / normalmode= 2s)
                long localTimeout= connectionTimeoutDefault_ms;

                if (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - getStartTime()) > localTimeout) {
                    //more then 2s has past since the last connectioncheck ping --> disconnect
                    try{
                        clientSocket.close();
                    }catch(IOException ex){
                        ex.printStackTrace();
                    } finally {
                        if (clientSocket.isClosed()) {
                            //PrintOnMonitor.printlnMon("Thread: " + unitReaderThread.getName() + ", gets interrupted, by connection lost!", PrintOnMonitor.Reason.THREAD);
                            unitReaderThread.interrupt();
                            writer = null;
                            reader = null;
                        }
                    }
                    notifyListener(ConnectionEvent.CONNECTION_LOST, 1);
                }
            }
            setConnection(false);
        }
    }

    /**
     * Methode reads the whole Protocol that has been received, until it gets the End of the Protocol that is "#". The Protocol looks like following:
     * It will call the readCommandFeedback to get the Commandnumber, the whole Protocol belongs to.
     * Then it will call
     *  -readCommandFeedbackValue (if just a Returnvalue is given)
     *  -readCommandFeedbackLogData (if the whole Loggingdatas are given)
     *  -readCommandFeedbackError (if an Error is given)
     *
     * Protocol without Logdatas (CommandFeedbackNo: 123 / ComandFeedbackValue: 1):
     *      #123/l/n
     *      1/l/n
     *      #
     *
     * * Protocol with Logdatas (CommandFeedbackNo: 2 / Datas: Header: timestamp;value1; + Logdatas: 2021-06-09_16:50:18;1.0;):
     *      #2/l/n
     *      timestamp;value1;
     *      2021-06-09_16:50:18;1.0;
     *      2021-06-09_16:50:29;0.0;
     *      2021-06-09_16:50:40;0.0;
     *      #
     *
     * ErrorProtocol without Logdatas (Error: E / ErrorNumberValue: 857):
     *      #E/l/n
     *      857/l/n
     *      #
     *
     * ConnectionCheck ping (just a "*")
     */
    private void readProtocol() {
        int readFeedback = 0;

        String commandfeedback = readCommandFeedback();
        if (commandfeedback.equals("")) {
            ; //ignored
        }else if (commandfeedback.equals("*")) {
            //Connectioncheck
            setStartTime();
            //PrintOnMonitor.printMon("*", PrintOnMonitor.Reason.CONNECTIONCHECK);
        }else if (commandfeedback.equals("E")) {
            //Errors of Unit
            //readFeedback = readError(Integer.parseInt(commandfeedback));
            setStartTime();
            if (readFeedback > -1) {
                notifyListener(ConnectionEvent.ERROR_RECEIVED, readFeedback);
            }
        }else if (commandfeedback.equals("2")) {
            //receive Logfile
            readFeedback = readLogData(Integer.parseInt(commandfeedback));
            setStartTime();
            if (readFeedback > -1) {
                notifyListener(ConnectionEvent.CMDFEEDBACK_RECEIVED, readFeedback);
            }
        }else {
            //all the commandos without any Datas transferred
            readFeedback = readValue(Integer.parseInt(commandfeedback));
            setStartTime();
            if (readFeedback  > -1 ) {
                notifyListener(ConnectionEvent.CMDFEEDBACK_RECEIVED, readFeedback);
            }
        }

    }

    /**
     * Methode reads the first line of a Protocol and gives out the Comandnumber that has been received.
     * @return command: the commandnumber to which the analysed protocol belonges to "123"= Commandnumber / "E"=Error (as String)
     */
    private String readCommandFeedback() {
        String serverFeedback = "";
        String command = "";

        if (isConnected()) {
            try {
                //read command feedback
                serverFeedback = reader.readLine();
                if (!serverFeedback.equals("*") && !serverFeedback.equals("")) {
                    command = serverFeedback.substring(1);
                    //PrintOnMonitor.printlnMon("Unit: " + getUnitName() + ", Commando-Feedback of Server: " + command, PrintOnMonitor.Reason.UNITINTERFACE);
                } else {
                    command = serverFeedback;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                //PrintOnMonitor.printMon(null, PrintOnMonitor.Reason.UNITINTERFACE);
            } finally {
                return command;
            }
        }
        return command;
    }

    /**
     * Methode reads the secound and last line of a Protocol and gives out the its value that belongs to CommandFeedback.
     * @param commandNo that got read in the previos methode (as int)
     * @return commandnumber if the end of the protocol got reached -1= not fullfilled / >0= fullfilled, commandno (as int)
     */
    private int readValue(int commandNo) {
        String serverFeedback = "";
        String value = "";
        int result = -1;
        boolean finishedRead = false;

        if (isConnected()) {
            while (!finishedRead) {
                try {
                    //read command feedback
                    serverFeedback = reader.readLine();

                    if (serverFeedback == null || !reader.ready()) {
                        finishedRead = true;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    //PrintOnMonitor.printMon(null, PrintOnMonitor.Reason.UNITINTERFACE);
                    break;
                }

                value = serverFeedback;
                if (value.equals("")) {
                    ; //ignored
                }else if (value.equals("*")) {
                    //Connectioncheck
                    setStartTime();
                    //PrintOnMonitor.printMon("*", PrintOnMonitor.Reason.CONNECTIONCHECK);
                }else if (value.equals("0")) {
                    //Commandfeedback value = false
                    //PrintOnMonitor.printMon("; Value: " + value, PrintOnMonitor.Reason.UNITINTERFACE);
                }else if (value.equals("1")) {
                    //Commandfeedback value = true
                    //PrintOnMonitor.printMon("; Value: " + value, PrintOnMonitor.Reason.UNITINTERFACE);
                }else if (value.equals("#")) {
                    //Protocolendline
                    //PrintOnMonitor.printMon("; end of Protocol reached!", PrintOnMonitor.Reason.UNITINTERFACE);
                    //PrintOnMonitor.printMon(null, PrintOnMonitor.Reason.UNITINTERFACE);
                    result = commandNo;
                } else {
                    //PrintOnMonitor.printMon("; not valid: " + value, PrintOnMonitor.Reason.UNITINTERFACE);
                }
            }
        }
        return result;
    }

    /**
     * Methode reads the secound and following lines of a Protocol and writes every line that got read into the file "datalog.txt" and into the ArrayList logDataList.
     * @param commandNo that got read in the previos methode (as int)
     * @return commandnumber if the end of the protocol got reached -1= not fullfilled / >0= fullfilled, commandno (as int)
     */
    private int readLogData(int commandNo) {
        String serverFeedback = "";
        String value = "";
        int result = -1;
        boolean finishedRead = false;

        //prepare the Loggingfile
        logfile.delete();
        try {
            logfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //prepare the ArrayList logDataList
        clearLogDataList();

        //read out the datas ans write it into the Loggingfile and the ArrayList logDataList
        if (isConnected()) {
            //PrintOnMonitor.printlnMon("; reading Loggdata of Unit (Encoding: "+ streamReader.getEncoding() +").", PrintOnMonitor.Reason.UNITINTERFACE);
            String line="";

            //PrintOnMonitor.printMon("Unit: " + getUnitName()  + ", String got read: ", PrintOnMonitor.Reason.UNITINTERFACE);
            while (!finishedRead) {
                try {
                    //read command feedback
                    line = reader.readLine();
                    //PrintOnMonitor.printMon(line + " ", PrintOnMonitor.Reason.UNITINTERFACE);

                    if (line == null || !reader.ready()) {
                        if (line.equals("#")) {
                            finishedRead = true;
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    break;
                }

                if (line != null) {
                    value = line;
                    if (value.equals("")) {
                        ; //ignored
                    } else if (value.equals("#")) {
                        //Protocolendline
                        //PrintOnMonitor.printMon("; end of Protocol reached!", PrintOnMonitor.Reason.UNITINTERFACE);
                        //PrintOnMonitor.printMon(null, PrintOnMonitor.Reason.UNITINTERFACE);
                        finishedRead = true;
                        result = commandNo;
                    } else {
                        //Loggingdata (write the Line into Loggingfile and ArrayList logDataList)
                        writeDatasIntoLoggingFile(line);
                        addLogLine(line);
                    }
                }
            }
        }
        return result;
    }


    /**
     * Methode writes the received Logdata into the File
     * @param receivedLogData (as String)
     */
    private void writeDatasIntoLoggingFile(String receivedLogData) {
        // Append flag is set to true
        try (FileWriter fw = new FileWriter(LoggingUnit.logfile, true)) {
            fw.write(receivedLogData);
            fw.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //------------------------------------------------------------------------------------------------------------------

    public class Connection implements Runnable{

        @Override
        public void run() {
            try {
                connectUnit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()) {
                ;
            }
            disconnectUnit();
        }

        /**
         * Methode tries to build up a connection to the Unit
         * @return connectionstate true=connected / false=disconnected (as boolean)
         */
        public boolean connectUnit() throws Exception{
            if (!isConnected()) {
                //Client Socket erzeugen, incl. Verbindungsanfrage
                try {
                    //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", try to connect to Server at: " + getIpAdress().toString().substring(1), PrintOnMonitor.Reason.CONNECTION);
                    clientSocket = new Socket(getIpAdress().toString().substring(1), serverPortNumber);

                    //clientSocket = new Socket(InetAddress.getByName("192.168.0.104"), serverPortNumber);
                    //clientSocket.setKeepAlive(false);

                    writer = new PrintWriter(clientSocket.getOutputStream());

                    streamReader = new InputStreamReader(clientSocket.getInputStream());
                    reader = new BufferedReader(streamReader);

                } catch (ConnectException e) {
                    //no Connection could have been established
                    throw e;
                }

                if (!clientSocket.isClosed()) {
                    //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", connection established!", PrintOnMonitor.Reason.CONNECTION);
                    //start the unitReaderThread
                    unitReaderThread = new Thread(new LoggingUnit.UnitReader());
                    unitReaderThread.setName("ReaderThreadUnit_" + getUnitName());
                    unitReaderThread.start();
                }

                try {
                    Thread.currentThread().sleep(200);
                } catch (IllegalArgumentException ignored) {
                    ;// to prevent Exception: "Not on FX application thread;"
                }

                //PrintOnMonitor.printlnMon("Unit: "+ getUnitName() + ", Connectionstate of connection to Server: " + isConnected(), PrintOnMonitor.Reason.CONNECTION);
            }
            return isConnected();
        }

        /**
         * Methode disconnects the connection to the Unit
         * @return connectionstate: true=connected / false=disconnected (as boolean)
         */
        public boolean disconnectUnit() {
            if (isConnected()) {
                try{
                    clientSocket.close();
                }catch(IOException ex){
                    ex.printStackTrace();
                } finally {
                    if (clientSocket.isClosed()) {
                        //PrintOnMonitor.printlnMon("Thread: " + unitReaderThread.getName() + ", gets interrupted, by disconnecting!", PrintOnMonitor.Reason.THREAD);
                        //interrupt the unitReaderThread
                        unitReaderThread.interrupt();
                        try {
                            unitReaderThread.join();
                            writer = null;
                            reader = null;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (!isConnected()) {
                //PrintOnMonitor.printlnMon("Unit: "+ getUnitName() + ", disconnected!", PrintOnMonitor.Reason.CONNECTION);
            } else {
                //PrintOnMonitor.printlnMon("Unit: "+ getUnitName() + ", Connectionstate of disconnection of Server: " + isConnected(), PrintOnMonitor.Reason.CONNECTION);
            }
            return isConnected();
        }

    }



}

