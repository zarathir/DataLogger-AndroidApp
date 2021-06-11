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
        unitHandler.addUnit(unit)
        units.value?.add(unit)
    }

    fun removeUnit(unit: LoggingUnit) {
        unitHandler.removeUnit(unit)
    }
}