package ght.app.datalogger

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.logSystem.UnitHandler
import java.lang.Exception


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
}