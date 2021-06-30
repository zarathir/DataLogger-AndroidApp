package ght.app.datalogger

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ght.app.datalogger.data.logSystem.EnumConnection
import ght.app.datalogger.data.logSystem.IntfGuiListener
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.logSystem.UnitHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    fun restoreUnits(context: Context) {
        unitHandler.readUnitsOfFile(context)
        units.value = unitHandler.unitArrayList
    }

    fun connectUnit(unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.connect()
        Thread.sleep(500)
        return if (unit.isConnected) {
            "Unit connected"
        } else {
            "Could not connect"
        }
    }

    fun disconnectUnit(unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)
        unit.disconnect()
        return if (!unit.isConnected) {
            "Could not disconnect unit"
        } else {
            "Unit disconnected"
        }
    }

    fun sendCommand(command: Int, unitName: String): String {
        val unit = unitHandler.getCertainUnit(unitName)

        return if (unit.isConnected) {
            when(command) {
                1 -> {
                    unit.sendCommand(123)
                    "Command 1 sent..."
                }
                2 -> {
                    unit.sendCommand(2)
                    "Command 2 sent..."
                }
                3 -> {
                    unit.sendCommand(3)
                    "Command 3 sent..."
                }
                else -> "Could not send command"
            }
        } else {
            "Unit is not connected"
        }
    }

    fun addListener(gl: IntfGuiListener, lue: IntfGuiListener.LogUnitEvent) {  // eng_gam testweise implementiert, Achtung, es sollte noch augewertet werden um welche Unit es sich handelt
        val unit = unitHandler.getCertainUnit("RobotDyn Wifi mit Poti")
        unit.addListener(gl, lue)
    }
}