package ght.app.datalogger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.units.UnitArduino
import ght.app.datalogger.data.units.UnitRaspberry

class UnitAdapter(private var list: MutableList<LoggingUnit>, private var onClickInterface: OnClickInterface)
    : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

    private var state: String = ""

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val unitNameTextView : TextView = view.findViewById(R.id.unit_name)
        val unitTypeTextView: TextView = view.findViewById(R.id.unit_type)
        val unitIpAddress : TextView = view.findViewById(R.id.unit_ip_address)
        val btnConnect : Button = view.findViewById(R.id.connect_button)
        val btnCmd1: Button = view.findViewById(R.id.command_button_1)
        val btnCmd2: Button = view.findViewById(R.id.command_button_2)
        val btnCmd3: Button = view.findViewById(R.id.command_button_3)
        val btnTrendView: Button = view.findViewById(R.id.trendview_button)
        val btnRemoveUnit: Button = view.findViewById(R.id.remove_button)

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
            state = if (!list[position].isConnected) {
                connectUnit(position)
            } else {
                disconnectUnit(position)
            }
        }

        holder.btnCmd1.setOnClickListener {
            state = sendCommand(1, position)
        }

        holder.btnCmd2.setOnClickListener {
            state = sendCommand(2, position)
        }

        holder.btnCmd3.setOnClickListener {
            state = sendCommand(3, position)
        }

        holder.btnTrendView.setOnClickListener {
            onClickInterface.setClick(holder.absoluteAdapterPosition, "trend")
        }

        holder.btnRemoveUnit.setOnClickListener {
            onClickInterface.setClick(holder.absoluteAdapterPosition, "remove")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateView(list: MutableList<LoggingUnit>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun getUnitType(unit: LoggingUnit): String {
        return when (unit) {
            is UnitArduino -> "Arduino"
            is UnitRaspberry -> "Raspberry"
            else -> ""
        }
    }

    private fun connectUnit(position: Int): String {
        list[position].connect()
        return if (list[position].isConnected) {
            "Unit connected"
        } else {
            "Could not connect"
        }
    }

    private fun disconnectUnit(position: Int): String {
        list[position].disconnect()

        return if (!list[position].isConnected) {
            "Could not disconnect unit"
        } else {
            "Unit disconnected"
        }
    }

    private fun sendCommand(id: Int, position: Int): String {

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
