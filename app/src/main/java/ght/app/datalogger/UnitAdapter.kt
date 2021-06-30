package ght.app.datalogger

import android.graphics.Color
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
            if (!list[position].isConnected){
                onClickInterface.setClick(holder.absoluteAdapterPosition,
                    OnClickInterface.Click.CONNECT)
                holder.btnConnect.text = "Disconnect"
                holder.btnConnect.setBackgroundColor(Color.rgb(115, 117, 251))
            } else {
                onClickInterface.setClick(holder.absoluteAdapterPosition,
                    OnClickInterface.Click.DISCONNECT)
                holder.btnConnect.text = "Connect"
                holder.btnConnect.setBackgroundColor(Color.rgb(98, 0, 238))
            }
        }

        holder.btnCmd1.setOnClickListener {
            onClickInterface.setClick(holder.absoluteAdapterPosition,
            OnClickInterface.Click.BUTTON1)
        }

        holder.btnCmd2.setOnClickListener {
            onClickInterface.setClick(holder.absoluteAdapterPosition,
            OnClickInterface.Click.BUTTON2)
        }

        holder.btnCmd3.setOnClickListener {
            onClickInterface.setClick(holder.absoluteAdapterPosition,
            OnClickInterface.Click.BUTTON3)
        }

        holder.btnTrendView.setOnClickListener {
            onClickInterface.setClick(holder.absoluteAdapterPosition,
            OnClickInterface.Click.TREND)
        }

        holder.btnRemoveUnit.setOnClickListener {
            onClickInterface.setClick(holder.absoluteAdapterPosition,
            OnClickInterface.Click.REMOVE)
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
}
