package ght.app.datalogger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.UnitAdapter.ViewHolder
import ght.app.datalogger.data.logSystem.IntfGuiListener
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.logSystem.PrintOnMonitor
import ght.app.datalogger.databinding.FragmentUnitListBinding
import android.content.Context
import android.content.ContextWrapper

/**
 * [Fragment] to display the units in a view and perform actions on each unit
 */
class UnitListFragment : Fragment(), IntfGuiListener {

    private var _binding: FragmentUnitListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val model: UnitViewModel by activityViewModels()
    private var adapter: UnitAdapter? = null
    private var onclickInterface: EventInterface? = null

    private var thiscontext: Context? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (container != null) {
            thiscontext = container.getContext()
        };
        _binding = FragmentUnitListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onclickInterface = object : EventInterface {
            override fun setClick(pos: Int, source: EventInterface.Click) {

                val unit = model.units.value?.get(pos)
                val unitName = unit!!.unitName

                when (source) {
                    EventInterface.Click.REMOVE -> {
                        model.setActiveUnit(unitName)
                        model.removeListener(
                            this@UnitListFragment,
                            IntfGuiListener.LogUnitEvent.CONNECTION_STATE,
                            unitName)
                        if (unit.isConnected) {
                            unit.disconnect()
                        }
                        model.deleteTrendDataFile()
                        model.removeUnit(unit)
                        adapter!!.notifyItemRemoved(pos)
                    }

                    EventInterface.Click.TREND -> {
                        model.setActiveUnit(unitName)
                        view.findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment
                        )
                    }

                    EventInterface.Click.CONNECT -> {
                        makeSnack(view, "Versuche zu verbinden...")
                        makeSnack(view, model.connectUnit(unitName))
                        //thiscontext?.let { model.sendCommand(2, unitName, it) }
                        adapter!!.notifyItemChanged(pos)
                        model.addListener(
                            this@UnitListFragment,
                            IntfGuiListener.LogUnitEvent.CONNECTION_LOST,
                            unitName)
                        model.addListener(
                            this@UnitListFragment,
                            IntfGuiListener.LogUnitEvent.CMDFEEDBACK_RECEIVED,
                            unitName)
                        model.addListener(
                            this@UnitListFragment,
                            IntfGuiListener.LogUnitEvent.ERROR_RECEIVED,
                            unitName)
                    }

                    EventInterface.Click.DISCONNECT -> {
                        makeSnack(view, model.disconnectUnit(unitName))
                        adapter!!.notifyItemChanged(pos)
                        model.removeListener(
                            this@UnitListFragment,
                            IntfGuiListener.LogUnitEvent.CONNECTION_LOST,
                            unitName)
                        model.removeListener(
                            this@UnitListFragment,
                            IntfGuiListener.LogUnitEvent.CMDFEEDBACK_RECEIVED,
                            unitName)
                        model.removeListener(
                            this@UnitListFragment,
                            IntfGuiListener.LogUnitEvent.ERROR_RECEIVED,
                            unitName)
                    }

                    EventInterface.Click.BUTTON1 -> {
                        thiscontext?.let { model.sendCommand(1, unitName, it) }?.let {
                            makeSnack(view,
                                it
                            )
                        }
                    }

                    EventInterface.Click.BUTTON2 -> {
                        thiscontext?.let { model.sendCommand(2, unitName, it) }?.let {
                            makeSnack(view,
                                it
                            )
                        }
                    }

                    EventInterface.Click.BUTTON3 -> {
                        thiscontext?.let { model.sendCommand(3, unitName, it) }?.let {
                            makeSnack(view,
                                it
                            )
                        }
                    }
                }
            }
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val units = model.units.value
        adapter = units?.let { UnitAdapter(it, onclickInterface as EventInterface) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val listObserver = Observer<MutableList<LoggingUnit>> { newList ->
            adapter?.updateView(newList)
        }

        model.getUnits().observe(viewLifecycleOwner, listObserver)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_addUnitFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun makeSnack(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
    /**
     * Function that triggers events received from the [IntfGuiListener]
     * @param lue [IntfGuiListener.LogUnitEvent]
     * @param value Value returned from the [LoggingUnit]
     * @param unitName Unit that returned the event
     */
    override fun loggingUnitEvent(
        lue: IntfGuiListener.LogUnitEvent,
        value: Int,
        unitName: String) {
        when (lue) {
            IntfGuiListener.LogUnitEvent.CONNECTION_STATE -> {
                Log.d("1","Listener CONNECTION_STATE got triggered!")
                PrintOnMonitor.printlnMon("Listener: $lue got triggerd with Value: $value , and UnitName: $unitName", PrintOnMonitor.Reason.LISTENER);
            }
            IntfGuiListener.LogUnitEvent.CONNECTION_LOST -> {
                activity?.runOnUiThread {
                    val unit = model.getUnit(unitName)
                    adapter!!.updateViewHolder(model.units.value!!.indexOf(unit))
                    model.removeListener(
                        this@UnitListFragment,
                        IntfGuiListener.LogUnitEvent.CONNECTION_LOST,
                        unitName)
                    this.view?.let {
                        Snackbar.make(
                            it,
                            "Unit $unitName hat die Verbindung verloren",
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
                Log.d("1","Listener CONNECTION_LOST got triggered!")
            }
            IntfGuiListener.LogUnitEvent.CMDFEEDBACK_RECEIVED -> {
                activity?.runOnUiThread {
                    val unit = model.getUnit(unitName)
                    adapter!!.updateViewHolder(model.units.value!!.indexOf(unit))
                    if (value == 2) {
                        val datapoints = unit.sizeLogDataList-1
                        Toast.makeText(context, unitName + ":\n $value empfangen \n ($datapoints Datenpunkte)", Toast.LENGTH_LONG).show()
                    }else {
                        Toast.makeText(context, unitName + ":\n $value empfangen", Toast.LENGTH_LONG).show()
                    }

                }
                Log.d("1","Listener CMDFEEDBACK_RECEIVED got triggered!")
            }
            IntfGuiListener.LogUnitEvent.ERROR_RECEIVED -> {
                //Log.d("1","Listener ERROR_RECEIVED got triggered!")
                PrintOnMonitor.printlnMon("Listener: $lue got triggerd with Value: $value , and UnitName: $unitName", PrintOnMonitor.Reason.LISTENER);
            }
        }
    }
}