package ght.app.datalogger

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.databinding.FragmentChartBinding

/**
 * This [Fragment] is for creating the chart view and display the data from the
 * chosen unit.
 */

class ChartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Resolve color from theme attr
        val primaryColor = MaterialColors.getColor(
            view, R.attr.colorSecondary
        )

        val model: UnitViewModel by activityViewModels()

        val chart = AnyChart.line()
        chart.title( "Trenddaten: " + model.getActiveUnit())
        chart.yAxis(0).title("Sensorwert")
        chart.xScroller(true);

        val logData = model.getTrendData()

        val data: MutableList<DataEntry> = ArrayList()

        for (i in 1 until logData.size) {
            if (logData[i].contains("*")) {
                continue
            } else {
                data.add(toDataEntry(logData[i]))
            }
        }

        // set first series data
        var line = chart.line(data);
        // set stroke thickness 5px and stroke color as darkOrange
        line.stroke("2 DarkOrange");

        if (data.isEmpty()) {
            data.add(ValueDataEntry("10-10-2020 10:10", 0))
            Snackbar.make(view, "Keine Daten vorhanden. " +
                    "Bitte zuerst verbinden und von Unit laden", Snackbar.LENGTH_LONG)
                .show()
        }

        chart.data(data)

        chart.draw(true)

        binding.chartView.setChart(chart)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toDataEntry(data: String): ValueDataEntry {
        val x = data.substringBefore(";")
        val value = data.substringAfter(";").toFloat()
        return ValueDataEntry(x, value)
    }

}