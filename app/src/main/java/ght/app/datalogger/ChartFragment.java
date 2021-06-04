package ght.app.datalogger;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ght.app.datalogger.databinding.FragmentChartBinding;


public class ChartFragment extends Fragment {

    private FragmentChartBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceType")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentChartBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view,
                              @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: Somehow the charts are not shown in a virtual device.
        //      Test on real device
        createTestChart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createChart() {
        Cartesian chart = AnyChart.line();

        chart.animation();
        chart.title("Test trendview");
        chart.yAxis(0).title("Sensorwert");
        chart.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("10-10-2020 10:10", 20));
        data.add(new ValueDataEntry("10-10-2020 10:11", 21));
        data.add(new ValueDataEntry("10-10-2020 10:12", 25));
        data.add(new ValueDataEntry("10-10-2020 10:13", 15));

        Set set = Set.instantiate();
        set.data(data);

        Mapping seriesMapping = set.mapAs("{x: 'x', value: 'value'");

        Line series = chart.line(seriesMapping);

        chart.legend().enabled();
        chart.legend().fontSize(13d);
        chart.legend().padding(0d, 0d, 10d, 0d);

        AnyChartView anyChartView = (AnyChartView) binding.chartView;
        anyChartView.setChart(chart);
    }

    private void createTestChart() {

        Log.i("CHART", "Creating Pie Chart");

        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("John", 10000));
        data.add(new ValueDataEntry("Jake", 12000));
        data.add(new ValueDataEntry("Peter", 18000));

        pie.data(data);

        AnyChartView anyChartView = (AnyChartView) binding.chartView;
        anyChartView.setChart(pie);

        Log.i("CHART", "Finished Chart");

    }
}

