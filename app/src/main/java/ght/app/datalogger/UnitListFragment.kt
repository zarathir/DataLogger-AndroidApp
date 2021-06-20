package ght.app.datalogger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            override fun setClick(pos: Int, source: String) {

                val unit = model.units.value?.get(pos)

                when (source) {
                    "remove" -> {
                        if (unit != null) {
                            model.removeUnit(unit)
                        }
                        adapter!!.notifyItemRemoved(pos)
                    }

                    "trend" -> {
                        model.setActiveUnit(unit!!.unitName)
                        view.findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment
                        )
                    }
                }
            }
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val units = model.units.value
        adapter = units?.let { UnitAdapter(it, onclickInterface as OnClickInterface) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)


        model.getUnits().observe(this, {
            adapter!!.notifyItemInserted(adapter!!.itemCount)
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_addUnitFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}