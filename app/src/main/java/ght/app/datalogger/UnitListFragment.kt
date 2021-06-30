package ght.app.datalogger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.databinding.FragmentUnitListBinding

/**
 * [Fragment] to display the units in a view and perform actions on each unit
 */
class UnitListFragment : Fragment(){

    private var _binding: FragmentUnitListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var adapter: UnitAdapter? = null
    private var onclickInterface: OnClickInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUnitListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: UnitViewModel by activityViewModels()

        onclickInterface = object : OnClickInterface {
            override fun setClick(pos: Int, source: OnClickInterface.Click) {

                val unit = model.units.value?.get(pos)
                val unitName = unit!!.unitName

                when (source) {
                    OnClickInterface.Click.REMOVE -> {
                        model.removeUnit(unit)
                        adapter!!.notifyItemRemoved(pos)
                    }

                    OnClickInterface.Click.TREND -> {
                        model.setActiveUnit(unitName)
                        view.findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment
                        )
                    }

                    OnClickInterface.Click.CONNECT -> {
                        makeSnack(view, model.connectUnit(unitName))
                    }

                    OnClickInterface.Click.DISCONNECT -> {
                        makeSnack(view, model.disconnectUnit(unitName))
                    }

                    OnClickInterface.Click.BUTTON1 -> {
                        makeSnack(view, model.sendCommand(1, unitName))
                    }

                    OnClickInterface.Click.BUTTON2 -> {
                        makeSnack(view, model.sendCommand(2, unitName))
                    }

                    OnClickInterface.Click.BUTTON3 -> {
                        makeSnack(view, model.sendCommand(3, unitName))
                    }
                }
            }
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val units = model.units.value
        adapter = units?.let { UnitAdapter(it, onclickInterface as OnClickInterface, view) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val listObserver = Observer<MutableList<LoggingUnit>> { newList ->
            adapter?.updateView(newList)
        }
        model.getUnits().observe(this, listObserver)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_addUnitFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun makeSnack(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}