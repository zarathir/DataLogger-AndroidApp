package ght.app.datalogger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
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

        createTestChart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createChart() {
        val chart = AnyChart.line()
        chart.animation()
        chart.title("Test trendview")
        chart.yAxis(0).title("Sensorwert")
        chart.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
        val data: MutableList<DataEntry> = java.util.ArrayList()
        data.add(ValueDataEntry("10-10-2020 10:10", 20))
        data.add(ValueDataEntry("10-10-2020 10:11", 21))
        data.add(ValueDataEntry("10-10-2020 10:12", 25))
        data.add(ValueDataEntry("10-10-2020 10:13", 15))
        val set = Set.instantiate()
        set.data(data)
        val seriesMapping = set.mapAs("{x: 'x', value: 'value'")
        val series = chart.line(seriesMapping)
        chart.legend().enabled()
        chart.legend().fontSize(13.0)
        chart.legend().padding(0.0, 0.0, 10.0, 0.0)
        binding.chartView.setChart(chart)
    }

    private fun createTestChart() {
        Log.i("CHART", "Creating Pie Chart")
        val pie = AnyChart.pie()
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("John", 10000))
        data.add(ValueDataEntry("Jake", 12000))
        data.add(ValueDataEntry("Peter", 18000))
        pie.data(data)
        binding.chartView.setChart(pie)
        Log.i("CHART", "Finished Chart")
    }
}