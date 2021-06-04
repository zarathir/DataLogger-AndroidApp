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


/**
 * This Class is the Superclass of all the differend Units,
 * it handles the connection / comands to the Unit / feedback, errors and the datestream from the Unit
 * @author M.Gasser
 * @version 1.000  04.05.2021
 */
public abstract class LoggingUnit implements Serializable {
    private static final long serialVersionID = 1L;

    private static final int serverPortNumber = 5001;

    private String unitName;
    private EnumUnits unitVendor;
    private transient boolean connected;

    private transient Socket clientSocket;
    private transient PrintWriter writer;
    private transient InputStreamReader streamReader;
    private transient BufferedReader reader;

    private transient Thread connectionThread;

    private transient List<IntfConnectionListener> connectionStateListeners;
    private transient List<IntfConnectionListener> connectionLostListeners;

    private transient static String pathToPackage = "DataLoggerApp/src/main/java/ght/app/files/";
    private transient static File logfile = new File(pathToPackage + "datalog.txt");


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
        this.clientSocket = null;
        this.writer = null;
        this.streamReader = null;
        this.reader = null;
        this.connectionThread = null;
        if (this instanceof UnitArduino) {
            this.unitVendor = EnumUnits.ARDUINO;
        } else if (this instanceof UnitRaspberry) {
            this.unitVendor = EnumUnits.RASPBERRY;
        }

        this.connectionStateListeners = new ArrayList<>();
        this.connectionLostListeners = new ArrayList<>();
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
    public void setConnection(boolean connectionState) {
        this.connected = connectionState;
        notifyListener(ConnectionEvent.CONNECTION_STATE, connectionState);
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
     * Methode tries to build up a connection to the Unit
     * @return connectionstate true=connected / false=disconnected (as boolean)
     */
    public boolean connect() throws Exception{
        if (!isConnected()) {
            //Client Socket erzeugen, incl. Verbindungsanfrage
            try {
                //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", try to connect to Server at: " + getIpAdress().toString().substring(1), PrintOnMonitor.Reason.CONNECTION);
                clientSocket = new Socket(getIpAdress().toString().substring(1), serverPortNumber); //"192.168.0.102",80);
                clientSocket.setKeepAlive(true);
            } catch (ConnectException e) {
                //no Connection could have been established
                throw e;
            } finally {
                if (!clientSocket.isClosed()) {
                    connectionThread = new Thread(new setConnectionState());
                    connectionThread.setName("ConnectThreadUnit_" + getUnitName());
                    //PrintOnMonitor.printlnMon("Thread: " + "ConnectThreadUnit_" + getUnitName() + " start", PrintOnMonitor.Reason.THREAD, PrintOnMonitor.Reason.CONNECTION);
                    connectionThread.start();
                }
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
    public boolean disconnect() {
        if (isConnected()) {
            try{
                //writer.close();
                //reader.close();
                clientSocket.close();
            }catch(IOException ex){
                ex.printStackTrace();
            } finally {
                if (clientSocket.isClosed()) {
                    connectionThread.interrupt();
                    try {
                        connectionThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //PrintOnMonitor.printlnMon("Unit: "+ getUnitName() + ", Connectionstate of disconnection of Server: " + isConnected(), PrintOnMonitor.Reason.CONNECTION);
        return isConnected();
    }

    /**
     * Methode sends a command number to the Unit
     * @param commandNo that has to be sent to the unit (as int)
     * @return writer state: true=all allright / false=Error occured or not unit connected (as boolean)
     */
    public boolean sendCommand(int commandNo) {
        if (isConnected()) {
            //write into Socket
            try {
                writer = new PrintWriter(clientSocket.getOutputStream());
                writer.println(commandNo);
                //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", Commando sent to Server: " + commandNo, PrintOnMonitor.Reason.UNITINTERFACE);
                //writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                boolean writerError = writer.checkError();
                if (writerError) {
                    //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", Writer set an error: ", PrintOnMonitor.Reason.UNITINTERFACE);
                }

                return !writerError;
            }
        }
        return false;
    }

    /**
     * Methode reads the feedback of a command, after a command has been sent to the Unit
     * @return writer state: true=all allright / false=Error occurred or not unit connected (as boolean)
     */
    public int readCommandFeedback() {
        String serverFeedback = "";
        int feedback = -1;

        if (isConnected()) {
            //write into Socket
            try {
                streamReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(streamReader);

                //read command feedback
                serverFeedback = reader.readLine();
                feedback = Integer.parseInt(serverFeedback);
                //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", Commando-Feedback of Server: " + feedback, PrintOnMonitor.Reason.UNITINTERFACE);
                //reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                return feedback;
            }
        }
        return feedback;
    }

    /**
     * Methode reads the whole Loggingfile, after the command has been sent to the Unit
     * @return writer state: true=all allright / false=Error occurred or not unit connected (as boolean)
     */
    public boolean readLoggingfile() {
        String serverFeedback = "";
        boolean feedback = false;

        logfile.delete();
        try {
            logfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isConnected()) {
            //in Socket schreiben
            if (streamReader == null) {
                try {
                    streamReader = new InputStreamReader(clientSocket.getInputStream());//, Charset.forName("ISO-8859-1"));
                    System.out.println("Encoding is: " + streamReader.getEncoding());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return feedback;
                }
            }
            if (reader == null){
                reader = new BufferedReader(streamReader);
            }

            //Meldung lesen

            /*String tmp="";
            boolean finishedReading = false;
            System.out.print("String got read: ");
            while (!finishedReading) {
                try {
                    tmp = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return feedback;
                }
                System.out.print(tmp + " ");
                if (tmp != null) {
                    //char tempchar = (char) tmp;
                    //String tempString = Character.toString(tempchar);
                    //serverFeedback += tempString;
                    writeDatasIntoLoggingFile(tmp);
                } else {
                    finishedReading = true;
                    System.out.print(" finish!");
                    feedback = true;
                }
            }*/

            String tmp="";
            System.out.print("String got read: ");
            while (true) {
                try {
                    if (!reader.ready()) {
                        System.out.println(" finish, break!");
                        feedback = true;
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return feedback;
                }

                try {
                    tmp = reader.readLine();
                    System.out.print(tmp + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                    return feedback;
                }

                if (tmp != null) {
                    writeDatasIntoLoggingFile(tmp);
                } else {
                    System.out.print(" finish!");
                    feedback = true;
                    break;
                }
            }
            //reader.close();

        }
        return feedback;
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
        }
    }

    /**
     * Methode to notify all the Listeners of the List "connectionStateListener" or "connectionLostListener".
     * The Listeners will get the Event itsself (as Enum) the value that has been given and the UnitName (as String)
     * @param ce (enum-value) of the Event that shall be given to the Listeners (as ConnectionEvent); the boolean value itsself (as boolean)
     */
    private void notifyListener(ConnectionEvent ce, boolean value) {
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
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    /*
     * This inner Class is constantly checking the connection to the certain Unit. It will throw an exeption
     * if a Unit gets is not reachable any more
     * @author M.Gasser
     * @version 1.000  20.05.2021
     */
    private class setConnectionState implements Runnable {
        private Socket s;

        /**
         * Methode will check the connection to the Unit every time when the Thread is awake (by calling of checkSocketConnection())
         * after a check it will bring the Thread into sleep
         */
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (checkSocketConnection()) {
                        setConnection(!clientSocket.isClosed());
                    }
                } catch (ExceptionConnectionLost ignored) {
                    ;
                } finally {
                    //PrintOnMonitor.printlnMon("Unit: " + getUnitName()  + ", check connection: " + isConnected(),PrintOnMonitor.Reason.CONNECTION);
                    //System.out.println("Unit: " + getUnitName()  + ", check connection: " + isConnected());
                    if (!Thread.currentThread().isInterrupted()) {
                        try {
                            //PrintOnMonitor.printlnMon("Thread: " + Thread.currentThread().getName() + ", goes to sleep!", PrintOnMonitor.Reason.THREAD);
                            //System.out.println("Thread: " + Thread.currentThread().getName() + ", goes to sleep!");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            setConnection(!clientSocket.isClosed());
                        }
                    }
                }
            }
            System.out.println("Thread: " + Thread.currentThread().getName() + ", interrupted!");
        }

        /**
         * Methode checks the connection to the Unit by establishing an new Socket-connection that will be closed if connection is ok.
         * if the connection is not ok a Exeption will be thrown and the Thread will interrupted
         * @return connectionstate: true=still connected / false=not connected anymore (as boolean)
         */
        private boolean checkSocketConnection() throws ExceptionConnectionLost {
            boolean connected = false;
            try {
                (s = new Socket(getIpAdress(), serverPortNumber)).close();
                setConnection(!clientSocket.isClosed());
                connected = true;
            } catch (IOException e) {
                //connection lost
                Thread.currentThread().interrupt();
                setConnection(false);
                notifyListener(ConnectionEvent.CONNECTION_LOST, true);
                throw new ExceptionConnectionLost(getUnitName());
            } finally {
                return connected;
            }
        }
    }
}


