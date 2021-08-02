package ght.app.datalogger

import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.units.UnitArduino
import ght.app.datalogger.data.units.UnitRaspberry
import kotlin.coroutines.coroutineContext

/**
 * This [UnitAdapter] is for displaying and handling events on the added unit items.
 */

class UnitAdapter(
    private var list: MutableList<LoggingUnit>,
    private var eventInterface: EventInterface)
    : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val unitNameTextView : TextView = view.findViewById(R.id.unit_name)
        val unitTypeTextView: TextView = view.findViewById(R.id.unit_type)
        val unitIpAddress : TextView = view.findViewById(R.id.unit_ip_address)
        val btnConnect : Button = view.findViewById(R.id.connect_button)
        val btnCmd1: Button = view.findViewById(R.id.command_button_1)
        val btnCmd2: Button = view.findViewById(R.id.command_button_2)
        val btnCmd3: Button = view.findViewById(R.id.command_button_3)
        val btnTrendView: ImageButton = view.findViewById(R.id.trendview_button)
        val btnRemoveUnit: ImageButton = view.findViewById(R.id.remove_button)
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

        if (list[position].isConnected) {
            holder.btnConnect.text = "Trennen"
            holder.btnCmd1.visibility = VISIBLE
            holder.btnCmd2.visibility = VISIBLE
            holder.btnCmd3.visibility = VISIBLE
        } else {
            holder.btnConnect.text = "Verbinden"
            holder.btnCmd1.visibility = INVISIBLE
            holder.btnCmd2.visibility = INVISIBLE
            holder.btnCmd3.visibility = INVISIBLE
        }

        /*if (list[position].logDataList.isEmpty()) {
            holder.btnTrendView.visibility = INVISIBLE
        } else {
            holder.btnTrendView.visibility = VISIBLE
        }*/

        holder.btnConnect.setOnClickListener {
            if (!list[position].isConnected){
                eventInterface.setClick(holder.absoluteAdapterPosition,
                    EventInterface.Click.CONNECT)
            } else {
                eventInterface.setClick(holder.absoluteAdapterPosition,
                    EventInterface.Click.DISCONNECT)
            }
        }

        holder.btnCmd1.setOnClickListener {
            eventInterface.setClick(holder.absoluteAdapterPosition,
            EventInterface.Click.BUTTON1)
        }

        holder.btnCmd2.setOnClickListener {
            eventInterface.setClick(holder.absoluteAdapterPosition,
            EventInterface.Click.BUTTON2)
        }

        holder.btnCmd3.setOnClickListener {
            eventInterface.setClick(holder.absoluteAdapterPosition,
            EventInterface.Click.BUTTON3)
        }

        holder.btnTrendView.setOnClickListener {
            eventInterface.setClick(holder.absoluteAdapterPosition,
            EventInterface.Click.TREND)
        }

        holder.btnRemoveUnit.setOnClickListener {
            eventInterface.setClick(holder.absoluteAdapterPosition,
            EventInterface.Click.REMOVE)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Function for updating the whole list with new items. Trigger after deserializing the units
     * @param list List of LoggingUnits
     */
    fun updateView(list: MutableList<LoggingUnit>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Function for updating the [ViewHolder] at the given position
     * @param position Position of item that has changed
     */
    fun updateViewHolder(position: Int) {
        notifyItemChanged(position)
    }

    private fun getUnitType(unit: LoggingUnit): String {
        return when (unit) {
            is UnitArduino -> "Arduino"
            is UnitRaspberry -> "Raspberry"
            else -> ""
        }
    }
}
