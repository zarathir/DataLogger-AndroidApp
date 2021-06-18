package ght.app.datalogger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import ght.app.datalogger.databinding.FragmentChartBinding

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

        val model: UnitViewModel by activityViewModels()

        val chart = AnyChart.line()
        chart.title("Test trendview")
        chart.yAxis(0).title("Sensorwert")

        //TODO("Implement method to get the log data from LoggingUnit")
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("10-10-2020 10:10", 20))
        data.add(ValueDataEntry("10-10-2020 10:11", 21))
        data.add(ValueDataEntry("10-10-2020 10:12", 25))
        data.add(ValueDataEntry("10-10-2020 10:13", 15))

        chart.data(data)

        chart.draw(true)

        binding.chartView.setChart(chart)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun StringToDataEntry(data: String): ValueDataEntry {
        val x = data.substringBefore(";")
        val value = data.substringAfter(";").toInt()
        return ValueDataEntry(x, value)
    }

}