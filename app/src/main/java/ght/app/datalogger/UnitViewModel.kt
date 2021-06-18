package ght.app.datalogger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.logSystem.UnitHandler


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

    fun getUnit(name: String) : LoggingUnit {
        return unitHandler.getCertainUnit(name)
    }

    fun addUnit(unit: LoggingUnit) {
        //TODO("Catch if unit already exists")
        unitHandler.addUnit(unit)
        units.value?.add(unit)
    }

    fun removeUnit(unit: LoggingUnit) {
        unitHandler.removeUnit(unit)
        units.value?.remove(unit)
    }

    fun setActiveUnit(unitName: String) {
        activeUnit = unitName
    }

    fun getTrendData(): ArrayList<String> {
        val unit = unitHandler.getCertainUnit(activeUnit)
        return unit.logDataList
    }
}