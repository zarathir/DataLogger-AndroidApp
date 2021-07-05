package ght.app.datalogger.data.units;

import ght.app.datalogger.data.logSystem.LoggingUnit;
import ght.app.datalogger.data.logSystem.PrintOnMonitor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


/**
 * This Class is a Test-Server to check if a connection can get established.
 * It should be started in an own Thread.
 * @author M.Gasser
 * @version 1.000  24.06.2021
 */
public class UnitServerArduino implements Runnable {
    private ServerSocket serverSock;

    //constructor
    public UnitServerArduino() {
        serverSock = null;
    }


    @Override
    public void run() {

        Socket sock = null;

        try {
            PrintOnMonitor.printMon("Try to start the Test-Server....  ", PrintOnMonitor.Reason.TESTSERVER);
            serverSock = new ServerSocket(LoggingUnit.getServerPortNumber());
            serverSock.setSoTimeout(100);
            PrintOnMonitor.printMon("started!", PrintOnMonitor.Reason.TESTSERVER);
            PrintOnMonitor.printMon(null, PrintOnMonitor.Reason.TESTSERVER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                sock = serverSock.accept();
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*if (sock != null) {
                try {
                    InputStream input = sock.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String text = reader.readLine();
                    PrintOnMonitor.printlnMon("Received of client: " + text, PrintOnMonitor.Reason.TESTSERVER);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/


            /*try {
                PrintWriter writer = new PrintWriter(sock.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //writer.println(tipp);
            //writer.close();*/
        }
        PrintOnMonitor.printlnMon("Test-Server stopped!", PrintOnMonitor.Reason.TESTSERVER);
        try {
            serverSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        UnitServerArduino server = new UnitServerArduino();
        Thread serverThread = new Thread(server);
        serverThread.setName("TestServerThread");
        serverThread.start();
    }

}