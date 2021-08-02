package ght.app.datalogger

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color.BLACK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.data.logSystem.IntfGuiListener
import ght.app.datalogger.data.logSystem.PrintOnMonitor
import ght.app.datalogger.databinding.FragmentChartBinding

/**
 * This [Fragment] is for creating the chart view and display the data from the
 * chosen unit.
 */

class ChartFragment : Fragment(), IntfGuiListener {

    private var _binding: FragmentChartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val model: UnitViewModel by activityViewModels()
    private var thiscontext: Context? = null;
    lateinit var chart: Cartesian;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            thiscontext = container.getContext()
        };

        _binding = FragmentChartBinding.inflate(inflater, container, false)

        chart = AnyChart.line()
        chart.background().fill(BLACK.toString(), 100);
        binding.chartView.setChart(chart)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Resolve color from theme attr
        val primaryColor = MaterialColors.getColor(
            view, R.attr.colorSecondary
        )

        val model: UnitViewModel by activityViewModels()

        chart = drawChart(chart, model, view)

        if (model.getConnectionState()) {
            binding.fabRefresh.show()
            model.getActiveUnit()?.let {
                model.addListener(
                    this@ChartFragment,
                    IntfGuiListener.LogUnitEvent.CONNECTION_LOST,
                    it
                )
                model.addListener(
                    this@ChartFragment,
                    IntfGuiListener.LogUnitEvent.CMDFEEDBACK_RECEIVED,
                    it
                )
            }
        } else {
            binding.fabRefresh.hide()
        }
        binding.fabRefresh.setOnClickListener {
            thiscontext?.let { model.getActiveUnit()?.let { it1 -> model.sendCommand(2, it1, it) } }


            //ObjectAnimator.ofFloat(binding.fabRefresh, "rotation", 0f, 360f).start();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    /**
     * Methode is updating the Chart with the received values of the LoggingUnit datas
     * @param chart that needs to be updated (should be the same chart as the one that is giving back to the at return-value)
     * @param model that contains the active Unit
     * @param view current view
     * @return the adress of the updated chart (this is not a new object, but the same as the one that has given into this Method as a param called chart)
     */
    private fun drawChart(chart: Cartesian, model: UnitViewModel, view: View) : Cartesian {
        //val chart = AnyChart.line()
        chart.title( model.getActiveUnit() + "  (" + (model.getTrendDataSize()-1) + " Datenpunkte)")
        chart.yAxis(0).title("Sensorwert")
        chart.xScroller(true);
        //chart.autoRedraw(true);

        // set first series data

        val data: MutableList<DataEntry> = getData()

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

        return chart
    }

    /**
     * Methode to get the Datas of the LoggingUnit-logDataList and writes into a MutableList
     * @return MutableList with all the Logdatas in it.
     */
    private fun getData() : MutableList<DataEntry> {
        val logData = model.getTrendData()

        val data: MutableList<DataEntry> = ArrayList()

        for (i in 1 until logData.size) {
            if (logData[i].contains("*")) {
                continue
            } else {
                data.add(toDataEntry(logData[i]))
            }
        }
        return data
    }

    private fun toDataEntry(data: String): ValueDataEntry {
        val x = data.substringBefore(";")
        val value = data.substringAfter(";").toFloat()
        return ValueDataEntry(x, value)
    }

    override fun loggingUnitEvent(
        lue: IntfGuiListener.LogUnitEvent?,
        value: Int,
        unitName: String?) {
        when (lue) {
            IntfGuiListener.LogUnitEvent.CONNECTION_LOST -> {
                binding.fabRefresh.hide()
                model.getActiveUnit()?.let {
                    model.removeListener(
                        this@ChartFragment,
                        IntfGuiListener.LogUnitEvent.CONNECTION_LOST,
                        it
                    )
                    model.removeListener(
                        this@ChartFragment,
                        IntfGuiListener.LogUnitEvent.CMDFEEDBACK_RECEIVED,
                        it
                    )
                }
            }
            IntfGuiListener.LogUnitEvent.CMDFEEDBACK_RECEIVED -> {
                if (value == 2) {
                    //Update Treandview if new Datas got received
                    view?.let { drawChart(chart, model, it) }
                    //ObjectAnimator.ofFloat(binding.fabRefresh, "rotation", 0f, 360f).end();
                }
            }
        }
    }

}