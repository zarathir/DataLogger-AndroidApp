package ght.app.datalogger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.databinding.FragmentUnitListBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class UnitListFragment : Fragment() {

    private var _binding: FragmentUnitListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


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

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val units = model.units.value
        val adapter = units?.let { UnitAdapter(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        model.getUnits().observe(this, Observer<MutableList<LoggingUnit>> {
            adapter!!.notifyItemInserted(adapter.itemCount)
        })


        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_addUnitFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}