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
import ght.app.datalogger.data.logSystem.IntfGuiListener
import ght.app.datalogger.data.logSystem.LoggingUnit
import ght.app.datalogger.data.logSystem.PrintOnMonitor
import ght.app.datalogger.databinding.FragmentAboutBinding

/**
 * [Fragment] to display About site
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.valueVersion.text = BuildConfig.VERSION_NAME;
        if (BuildConfig.VERSION_CODE.toString().length == 1) {
            binding.valueVersionsCode.text = "V00" + BuildConfig.VERSION_CODE.toString();
        } else if (BuildConfig.VERSION_CODE.toString().length == 2) {
            binding.valueVersionsCode.text = "V0" + BuildConfig.VERSION_CODE.toString();
        } else {
            binding.valueVersionsCode.text = "V" + BuildConfig.VERSION_CODE.toString();
        }

        /*binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_AboutFragment_to_FirstFragment)
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}