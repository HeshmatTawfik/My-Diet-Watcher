package com.heshmat.mydietwatcher.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.LoadingDialog;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.models.TodayPlan;
import com.heshmat.mydietwatcher.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsticsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @BindView(R.id.chart)
    BarChart barChart;

    public StatsticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsticsFragment newInstance(String param1, String param2) {

        return new StatsticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statstics, container, false);
        ButterKnife.bind(this, view);
        Context context = this.getContext();
        LoadingDialog loadingDialog = new LoadingDialog(this.getActivity());
        loadingDialog.startLoadingDialog();

        DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                .collection(StaticFields.TODAY_PLAN_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<TodayPlan> todayplans = queryDocumentSnapshots.toObjects(TodayPlan.class);
                if (!todayplans.isEmpty()) {
                    drawChart(todayplans);
                } else {
                    new AlertDialog.Builder(context).setMessage("You have no statistics yet").
                            setPositiveButton("Ok", null).show();
                }

            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                loadingDialog.dismissDialog();
            }
        });


        return view;
    }

    public void drawChart(List<TodayPlan> goals) {
        ArrayList<BarEntry> goalsEntries = new ArrayList<>();
        ArrayList<BarEntry> ateFood = new ArrayList<>();
        for (int i = 0; i < goals.size(); i++) {
            BarEntry barEntry = new BarEntry(i, (float) goals.get(i).getDiet().getTotalCalories());

            goalsEntries.add(new BarEntry(i, (float) goals.get(i).getDiet().getTotalCalories()));
            ateFood.add(new BarEntry(i, (float) goals.get(i).totalKcal()));
        }

        BarDataSet set1 = new BarDataSet(goalsEntries, "Goal");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarDataSet set2 = new BarDataSet(ateFood, "Eaten");
        set2.setStackLabels(new String[]{"Stack 1", "Stack 2"});
        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255));
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(10f);

        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        float groupSpace = 0.06f;
        float barSpace = 0.02f;
        float barWidth = 0.2f;
        BarData data = new BarData(set1, set2);
        data.setBarWidth(barWidth);

        data.groupBars(0, groupSpace, barSpace);
        barChart.setData(data);
        barChart.invalidate();


    }
}
