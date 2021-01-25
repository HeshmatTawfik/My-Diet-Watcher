package com.heshmat.mydietwatcher.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.adapters.AddFoodAdapter;
import com.heshmat.mydietwatcher.models.Diet;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DietDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DietDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.proteinDietDetailsTv)
    TextView protiensTv;
    @BindView(R.id.kcalsDietDetailsTv)
    TextView kcalTv;
    @BindView(R.id.fatDietDetailsTv)
    TextView fats;
    @BindView(R.id.dietDetailFoodRv)
    RecyclerView rv;
    AddFoodAdapter mAddFoodAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DietDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DietDetailsFragment.
     */
    public static DietDetailsFragment newInstance(String param1, String param2) {
        DietDetailsFragment fragment = new DietDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static DietDetailsFragment newInstance() {

        return new DietDetailsFragment();
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

        View view= inflater.inflate(R.layout.fragment_diet_details, container, false);
        ButterKnife.bind(this, view);
        DecimalFormat df = new DecimalFormat("#.##");
        kcalTv.setText(df.format(Diet.currentCreatedDiet.getTotalCalories()));
        protiensTv.setText(df.format(Diet.currentCreatedDiet.totalProteins()));
        fats.setText(df.format(Diet.currentCreatedDiet.totalFats()));
        mAddFoodAdapter = new AddFoodAdapter(this.getContext(), Diet.currentCreatedDiet.getFoods());
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(llm);
        rv.setAdapter(mAddFoodAdapter);
        rv.setAnimation(null);

        return view;
    }
}
