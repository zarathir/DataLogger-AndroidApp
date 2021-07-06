package ght.app.datalogger

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.data.logSystem.EnumConnection
import ght.app.datalogger.data.logSystem.EnumUnits
import ght.app.datalogger.data.logSystem.IntfGuiListener
import ght.app.datalogger.data.units.UnitArduino
import ght.app.datalogger.data.units.UnitRaspberry
import ght.app.datalogger.databinding.FragmentAddUnitBinding
import java.net.Inet4Address
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

        // Create dropdown menu for unit type
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
                when(position) {
                    0 -> selectedUnitType = EnumUnits.ARDUINO
                    1 -> selectedUnitType = EnumUnits.RASPBERRY
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Create dropdown menu for interface type
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
                when(position) {
                    1 -> selectedIntfType = EnumConnection.WIFI
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Create unit with the given informations
        binding.buttonAdd.setOnClickListener {

            val ipAddress = binding.editTextIpAddress.text.toString()
            val unitExists = "Unit existiert bereits"

            if (Patterns.IP_ADDRESS.matcher(ipAddress).matches()) {
                when(selectedUnitType) {
                    EnumUnits.RASPBERRY -> if (model.addUnit(UnitRaspberry(binding.editTextName.text.toString(),
                            InetAddress.getByName(ipAddress),
                            selectedIntfType))) {
                        findNavController().navigate(R.id.action_addUnitFragment_to_FirstFragment)
                    } else {
                        makeSnack(view, unitExists)
                    }

                    EnumUnits.ARDUINO -> if (model.addUnit(UnitArduino(binding.editTextName.text.toString(),
                            InetAddress.getByName(ipAddress),
                            selectedIntfType))) {
                        findNavController().navigate(R.id.action_addUnitFragment_to_FirstFragment)
                    } else {
                        makeSnack(view, unitExists)
                    }
                }
            } else {
                makeSnack(view, "Falsche IP Adresse eingegeben")
            }

            hideKeyboard()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun makeSnack(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val manager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.hideSoftInputFromWindow(
            requireActivity()
                .findViewById<View>(android.R.id.content).windowToken, 0
        )

        binding.editTextIpAddress.clearFocus()
    }
}