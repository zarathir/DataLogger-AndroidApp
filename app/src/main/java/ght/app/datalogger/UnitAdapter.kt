package ght.app.datalogger

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.data.logSystem.LoggingUnit

class UnitAdapter(private val list: MutableList<LoggingUnit>)
    : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val unitNameTextView : TextView = view.findViewById(R.id.unit_name)
        val unitIpAddress : TextView = view.findViewById(R.id.unit_ip_address)
        val unitCmd : Button = view.findViewById(R.id.command_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.logging_unit_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.unitNameTextView.text = list[position].unitName
        holder.unitIpAddress.text = list[position].ipAdress.toString()
        if(holder.itemView.isActivated) {
            Log.i("HOLDER", "isActivated")
            Snackbar.make(holder.itemView, "Activated: " + holder.unitNameTextView.text,
                Snackbar.LENGTH_LONG).setAction("no Action", null).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
