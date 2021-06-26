package ght.app.datalogger.data.logSystem;

/**
 * This Interface should be implemented into the GUi-class, as well as in the LoggingUnit.
 * It will inform the Gui about any Event that happend on the LoggingUnit.
 * @author M.Gasser
 * @version 1.000  05.05.2021
 */
public interface IntfGuiListener {

    //Events that Listener IntfGuiListener contains:
    enum LogUnitEvent {
        CONNECTION_STATE, CONNECTION_LOST, CMDFEEDBACK_RECEIVED, ERROR_RECEIVED;
    }

    /**
     * This Methode contains the Action that will be run, if one of the Listeners:
     * (connectionStateListener/connectionLostListener/comandReceivedListeners/errorReceivedListeners) gets notified,
     * with the containing Informations:
     * @param lue (Loggingunit event) event-enum of the Event that shall be given to the Listeners (as LogUnitEvent);
     * @param value the boolean value itsself (as int);
     */
    void loggingUnitEvent(LogUnitEvent lue, int value, String unitName);
}