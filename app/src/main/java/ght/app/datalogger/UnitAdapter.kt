package ght.app.datalogger

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.units.UnitArduino
import ght.app.datalogger.data.units.UnitRaspberry

class UnitAdapter(private val list: MutableList<LoggingUnit>)
    : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val unitNameTextView : TextView = view.findViewById(R.id.unit_name)
        val unitTypeTextView: TextView = view.findViewById(R.id.unit_type)
        val unitIpAddress : TextView = view.findViewById(R.id.unit_ip_address)
        val btnConnect : Button = view.findViewById(R.id.connect_button)
        val btnCmd1: Button = view.findViewById(R.id.command_button_1)
        val btnCmd2: Button = view.findViewById(R.id.command_button_2)
        val btnCmd3: Button = view.findViewById(R.id.command_button_3)
        val btnTrendView: Button = view.findViewById(R.id.trendview_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.logging_unit_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.unitNameTextView.text = list[position].unitName
        holder.unitTypeTextView.text = getUnitType(list[position])
        val ipAddressText = list[position].ipAdress.toString()
        holder.unitIpAddress.text = ipAddressText.subSequence(1, ipAddressText.length )

        holder.btnConnect.setOnClickListener {
            //connectUnit(position)
            val unit = list[position]
            Log.i("EVENT", "Clicked: {}$unit connect")
        }

        holder.btnCmd1.setOnClickListener {
            //sendCommand(1, position)
            val unit = list[position]
            Log.i("EVENT", "Clicked: {}$unit command 1")
        }

        holder.btnTrendView.setOnClickListener {

            it.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getUnitType(unit: LoggingUnit): String {
        return when (unit) {
            is UnitArduino -> "Arduino"
            is UnitRaspberry -> "Raspberry"
            else -> ""
        }
    }

    fun connectUnit(position: Int): String {
        list[position].connect()
        return if (list[position].isConnected) {
            "Unit connected"
        } else {
            "Could not connect"
        }
    }

    fun disconnectUnit(position: Int): String {
        list[position].disconnect()

        return if (!list[position].isConnected) {
            "Could not disconnect unit"
        } else {
            "Unit disconnected"
        }
    }

    fun sendCommand(id: Int, position: Int): String {

        return if (list[position].isConnected) {
            when(id) {
                1 -> {list[position].sendCommand(123)
                    "Command 1 sent..."}
                2 -> {list[position].sendCommand(2)
                    "Command 2 sent..."}
                3 -> {list[position].sendCommand(3)
                    "Command 3 sent..."}
                else -> "Could not send command"
            }
        } else
            "Unit is not connected"
    }
}
