package ght.app.datalogger

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.UnitAdapter.ViewHolder
import ght.app.datalogger.data.logSystem.*
import java.lang.Exception
import kotlin.collections.ArrayList


/**
 * [ViewModel] for interfacing with the backend code
 */
class UnitViewModel : ViewModel() {

    var units = MutableLiveData<MutableList<LoggingUnit>>()
    private val unitHandler = UnitHandler()
    private var activeUnit: String? = null

    init {
        units.value = unitHandler.unitArrayList.toMutableList()
    }

    /**
     * Returns the units stored as [LiveData]
     * @return [LiveData] of [MutableList] of [LoggingUnit]'s
     */
    fun getUnits() : LiveData<MutableList<LoggingUnit>> {
        return units
    }

    /**
     * Returns a single unit
     * @param unitName Unit name of the [LoggingUnit]
     * @return Requested [LoggingUnit]
     */
    fun getUnit(unitName: String): LoggingUnit {
        return unitHandler.getCertainUnit(unitName)
    }

    /**
     * Adds a [LoggingUnit] to the [UnitHandler]
     * @param unit [LoggingUnit] to add
     * @return True if unit was successfully added to the [UnitHandler]
     */
    fun addUnit(unit: LoggingUnit): Boolean {
        return try {
            unitHandler.addUnit(unit)
            units.value = unitHandler.unitArrayList
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Removes a [LoggingUnit] from the [UnitHandler]
     * @param unit [LoggingUnit] to remove
     * @return True if unit was successfully removed from the [UnitHandler]
     */
    fun removeUnit(unit: LoggingUnit): Boolean {
        return try {
            unitHandler.removeUnit(unit)
            units.value = unitHandler.unitArrayList
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Sets the active unit to handle the transition to the [ChartFragment]
     * @param unitName Unit name to set
     */
    fun setActiveUnit(unitName: String) {
        activeUnit = unitName
    }

    /**
     * Gets the active unit to handle the transition to the [ChartFragment]
     * @return Unit name of the active unit
     */
    fun getActiveUnit() : String? {
        return activeUnit
    }

    /**
     * Gets the trend data from the requested [LoggingUnit]
     * @return [ArrayList]<[String]> with log data format
     */
    fun getTrendData(): ArrayList<String> {
        val unit = unitHandler.getCertainUnit(activeUnit)
        return unit.logDataList
    }

    /**
     * Safe all added [LoggingUnit]'s to the [UnitHandler]
     * @param context App [Context] for creating the file in the app directory
     */
    fun safeUnits(context: Context) {
        unitHandler.writeUnitsIntoFile(context)
    }

    /**
     * Restore all added [LoggingUnit]'s to the [UnitHandler]
     * @param context App [Context] for reading the file from the app directory
     */
    fun restoreUnits(context: Context) : String {
        return try {
            unitHandler.readUnitsOfFile(context)
            units.value = unitHandler.unitArrayList
            "Units erfolgreich geladen"
        } catch (e: Exception) {
            "Units konnten nicht geladen werden"
        }
    }

    /**
     * Connect to [LoggingUnit]
     * @param unitName Unit name of the [LoggingUnit] to connect to
     */
    fun connectUnit(unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)
        return try {
            unit.connect()
            "Unit $unitName erfolgreich verbunden"
        } catch (e: Exception) {
            "Unit $unitName konnte nicht verbunden werden"
        }
    }

    /**
     * Disconnect from [LoggingUnit]
     * @param unitName Unit name of the [LoggingUnit] to disconnect from
     */
    fun disconnectUnit(unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.disconnect()
        return if (unit.isConnected) {
            "Unit $unitName konnte nicht getrennt werden"
        } else {
            "Unit $unitName erfolgreich getrennt"
        }
    }

    /**
     * Send a command to the desired [LoggingUnit]
     * @param command Command to send
     * @param unitName Unit name of the [LoggingUnit]
     * @return Message which command was sent to the [LoggingUnit]
     */
    fun sendCommand(command: Int, unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)

        return if (unit.isConnected) {
            when(command) {
                1 -> {
                    unit.sendCommand(123)
                    "Kommando 1 gesendet..."
                }
                2 -> {
                    unit.sendCommand(2)
                    "Kommando 2 gesendet..."
                }
                3 -> {
                    unit.sendCommand(3)
                    "Kommando 3 gesendet..."
                }
                else -> "Kommando konnte nicht abgesetzt werden"
            }
        } else {
            "Unit $unitName ist nicht verbunden"
        }
    }

    /**
     * Add a [IntfGuiListener] to a [LoggingUnit]
     * @param gl [IntfGuiListener]
     * @param lue Which [IntfGuiListener.LogUnitEvent] to listen to
     * @param unitName [LoggingUnit] to add the [IntfGuiListener] to
     */
    fun addListener(gl: IntfGuiListener, lue: IntfGuiListener.LogUnitEvent, unitName: String) {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.addListener(gl, lue)
    }

    /**
     * Remove a [IntfGuiListener] from a [LoggingUnit]
     * @param gl [IntfGuiListener]
     * @param lue Which [IntfGuiListener.LogUnitEvent] to remove the listener from
     * @param unitName [LoggingUnit] to remove the [IntfGuiListener] from
     */
    fun removeListener(gl: IntfGuiListener, lue: IntfGuiListener.LogUnitEvent, unitName: String) {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.removeListener(gl, lue)
    }

}