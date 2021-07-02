package ght.app.datalogger

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
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

    fun getUnits() : LiveData<MutableList<LoggingUnit>> {
        return units
    }

    fun getUnit(unitName: String): LoggingUnit {
        return unitHandler.getCertainUnit(unitName)
    }

    fun addUnit(unit: LoggingUnit): Boolean {
        return try {
            unitHandler.addUnit(unit)
            units.value = unitHandler.unitArrayList
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeUnit(unit: LoggingUnit): Boolean {
        return try {
            unitHandler.removeUnit(unit)
            units.value = unitHandler.unitArrayList
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setActiveUnit(unitName: String) {
        activeUnit = unitName
    }

    fun getActiveUnit() : String? {
        return activeUnit
    }

    fun getTrendData(): ArrayList<String> {
        val unit = unitHandler.getCertainUnit(activeUnit)
        return unit.logDataList
    }

    fun safeUnits(context: Context) {
        unitHandler.writeUnitsIntoFile(context)
    }

    fun restoreUnits(context: Context) : String {
        return try {
            unitHandler.readUnitsOfFile(context)
            units.value = unitHandler.unitArrayList
            "Units erfolgreich geladen"
        } catch (e: Exception) {
            "Units konnten nicht geladen werden"
        }
    }

    fun connectUnit(unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)
        return try {
            unit.connect()
            "Unit $unitName erfolgreich verbunden"
        } catch (e: Exception) {
            "Unit $unitName konnte nicht verbunden werden"
        }
    }

    fun disconnectUnit(unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.disconnect()
        //Thread.sleep(200)
        return if (unit.isConnected) {
            "Unit $unitName konnte nicht getrennt werden"
        } else {
            "Unit $unitName erfolgreich getrennt"
        }
    }

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

    fun addListener(gl: IntfGuiListener, lue: IntfGuiListener.LogUnitEvent, unitName: String) {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.addListener(gl, lue)
    }

    fun removeListener(gl: IntfGuiListener, lue: IntfGuiListener.LogUnitEvent, unitName: String) {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.removeListener(gl, lue)
    }

}