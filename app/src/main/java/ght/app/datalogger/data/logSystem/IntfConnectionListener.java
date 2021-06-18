package ght.app.datalogger.data.logSystem;

public interface IntfConnectionListener {
    enum ConnectionEvent {
        CONNECTION_STATE, CONNECTION_LOST, CMDFEEDBACK_RECEIVED, ERROR_RECEIVED;
    }

    void connectionEvent(ConnectionEvent e, int value, String unitName);
}