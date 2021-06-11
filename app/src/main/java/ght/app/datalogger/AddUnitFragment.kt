package ght.app.datalogger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ght.app.datalogger.data.logSystem.EnumConnection
import ght.app.datalogger.data.logSystem.EnumUnits
import ght.app.datalogger.data.units.UnitArduino
import ght.app.datalogger.databinding.FragmentAddUnitBinding
import ght.app.datalogger.databinding.FragmentChartBinding
import java.net.InetAddress

/**
 * This [Fragment] is for creating new Units and adding them
 * to the [UnitViewModel].
 */
class AddUnitFragment : Fragment() {

    private var _binding: FragmentAddUnitBinding? = null

    private val binding get() = _binding!!

    private var selectedUnitType : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddUnitBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: UnitViewModel by activityViewModels()


        val spinnerUnits : Spinner = view.findViewById(R.id.dropdown_unit_type)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnits.adapter = adapter
        }
        spinnerUnits.onItemSelectedListener

        val spinnerInterface : Spinner = view.findViewById(R.id.dropdown_interface_type)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.units_interface,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerInterface.adapter = adapter
        }

        binding.buttonAdd.setOnClickListener {
            val unit = UnitArduino(binding.editTextName.text.toString(),
            InetAddress.getByName(binding.editTextIpAddress.text.toString()),
            EnumConnection.WIFI)

            model.addUnit(unit)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SpinnerAdapter() : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            when(parent.getItemAtPosition(position)) {
                1 -> Log.i("VALUE", EnumUnits.RASPBERRY.toString())
                2 -> Log.i("VALUE", EnumUnits.ARDUINO.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}
