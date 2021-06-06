package ght.app.datalogger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ght.app.datalogger.data.logSystem.LoggingUnit

class UnitAdapter(private val list: List<LoggingUnit>)
    : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.logging_unit_cell, parent, false)) {

        init {
            TODO("Implement Loggingunit structure")
        }

        fun bind(Unit: LoggingUnit) {
            TODO("Implement Loggingunit structure")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie: LoggingUnit = list[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
