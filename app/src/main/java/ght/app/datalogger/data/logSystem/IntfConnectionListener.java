package ght.app.datalogger.data.logSystem;

public interface IntfConnectionListener {
    enum ConnectionEvent {
        CONNECTION_STATE, CONNECTION_LOST;
    }

    void connectionEvent(ConnectionEvent e, boolean value, String unitName);
}
