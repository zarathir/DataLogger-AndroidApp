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
import androidx.navigation.fragment.findNavController
import ght.app.datalogger.data.logSystem.EnumConnection
import ght.app.datalogger.data.logSystem.EnumUnits
import ght.app.datalogger.data.units.UnitArduino
import ght.app.datalogger.data.units.UnitRaspberry
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

    private lateinit var selectedUnitType : EnumUnits
    private lateinit var selectedIntfType : EnumConnection

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
        selectedUnitType = EnumUnits.ARDUINO
        selectedIntfType = EnumConnection.WIFI


        val spinnerUnits : Spinner = view.findViewById(R.id.dropdown_unit_type)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnits.adapter = adapter
        }

        spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    when(parent.getItemAtPosition(position)) {
                        1 -> selectedUnitType = EnumUnits.RASPBERRY
                        2 -> selectedUnitType = EnumUnits.ARDUINO
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val spinnerInterface : Spinner = view.findViewById(R.id.dropdown_interface_type)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.units_interface,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerInterface.adapter = adapter
        }

        spinnerInterface.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    when(parent.getItemAtPosition(position)) {
                        1 -> selectedIntfType = EnumConnection.WIFI
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        binding.buttonAdd.setOnClickListener {

            when(selectedUnitType) {
                EnumUnits.RASPBERRY -> model.addUnit(UnitRaspberry(binding.editTextName.text.toString(),
                    InetAddress.getByName(binding.editTextIpAddress.text.toString()),
                    selectedIntfType))

                EnumUnits.ARDUINO -> model.addUnit(UnitArduino(binding.editTextName.text.toString(),
                    InetAddress.getByName(binding.editTextIpAddress.text.toString()),
                    selectedIntfType))
            }

            findNavController().navigate(R.id.action_addUnitFragment_to_FirstFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}