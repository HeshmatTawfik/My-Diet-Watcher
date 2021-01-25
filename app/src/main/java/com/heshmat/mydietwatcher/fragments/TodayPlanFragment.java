package com.heshmat.mydietwatcher.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.FormattingDate;
import com.heshmat.mydietwatcher.ProgressBarAnimation;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.adapters.AddFoodAdapter;
import com.heshmat.mydietwatcher.adapters.TodayPlanFoodAdapter;
import com.heshmat.mydietwatcher.models.Food;
import com.heshmat.mydietwatcher.models.TodayPlan;
import com.heshmat.mydietwatcher.models.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayPlanFragment extends Fragment {
    @BindView(R.id.nutrationTodayPlansLL)
    LinearLayout linearLayout;
    @BindView(R.id.proteinTodayPlansTv)
    TextView protiensTv;
    @BindView(R.id.kcalsTodayPlansTv)
    TextView kcalTv;
    @BindView(R.id.fatTodayPlansTv)
    TextView fats;
    @BindView(R.id.todayPlanFoodRv)
    RecyclerView rv;
    @BindView(R.id.todayPlanProgressPar)
    ProgressBar progressBar;
    TodayPlanFoodAdapter mAdapter;
    @BindView(R.id.progressLL)
    LinearLayout progressLL;
    @BindView(R.id.progressTv)
    TextView progressTv;

    @BindView(R.id.proteinTodayPlansAteTv)
    TextView protiensAteTv;
    @BindView(R.id.kcalsTodayPlansAteTv)
    TextView kcalAteTv;
    @BindView(R.id.fatTodayPlansAteTv)
    TextView fatsAte;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;


    public TodayPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayPlanFragment.
     */
    public static TodayPlanFragment newInstance(String param1, String param2) {
        TodayPlanFragment fragment = new TodayPlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static TodayPlanFragment newInstance() {
        return new TodayPlanFragment();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    List<Food> mFoodList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_plan, container, false);
        ButterKnife.bind(this, view);
        mFoodList = new ArrayList<>();
        mAdapter = new TodayPlanFoodAdapter(this.getContext(), mFoodList);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(llm);
        rv.setAdapter(mAdapter);
        rv.setAnimation(null);

        DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION)
                .document(User.currentUser.getId()).collection(StaticFields.TODAY_PLAN_COLLECTION)
                .document(FormattingDate.formattedDate(new Date(System.currentTimeMillis())))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    TodayPlan todayPlan = documentSnapshot.toObject(TodayPlan.class);
                    if (todayPlan.getEatenFood() != null && !todayPlan.getEatenFood().isEmpty()) {

                        progressLL.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setMax(100);
                        int percent = (int) ((100 * todayPlan.totalKcal()) / todayPlan.getDiet().getTotalCalories());
                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, percent,progressTv);
                        if (percent > 100)
                            progressBar.getProgressDrawable().setColorFilter(
                                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                        anim.setDuration(3000);
                        progressBar.startAnimation(anim);
                        DecimalFormat df = new DecimalFormat("#.##");
                        kcalTv.setText(df.format(todayPlan.getDiet().getTotalCalories()));
                        protiensTv.setText(df.format(todayPlan.getDiet().totalProteins()));
                        fats.setText(df.format(todayPlan.getDiet().totalFats()));

                        kcalAteTv.setText(df.format(todayPlan.totalKcal()));
                        protiensAteTv.setText(df.format(todayPlan.totalProteins()));
                        fatsAte.setText(df.format(todayPlan.totalFats()));

                        mFoodList.addAll(todayPlan.getEatenFood().values());
                        mAdapter.notifyDataSetChanged();

                    } else {
                        new AlertDialog.Builder(TodayPlanFragment.this.getContext()).setMessage("You haven't eat anything today ")
                                .setPositiveButton("Ok", null).show();
                    }


                } else {
                    new AlertDialog.Builder(TodayPlanFragment.this.getContext()).setMessage("You haven't set today plan yet")
                            .setPositiveButton("Ok", null).show();
                }

            }
        });


        return view;
    }

}
