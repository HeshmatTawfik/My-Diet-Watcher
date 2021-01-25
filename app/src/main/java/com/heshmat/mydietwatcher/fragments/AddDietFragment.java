package com.heshmat.mydietwatcher.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.LoadingDialog;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.RetrofitInit;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.adapters.AddFoodAdapter;
import com.heshmat.mydietwatcher.adapters.SearchFoodAdapter;
import com.heshmat.mydietwatcher.models.Diet;
import com.heshmat.mydietwatcher.models.EdamamModel;
import com.heshmat.mydietwatcher.models.Food;
import com.heshmat.mydietwatcher.models.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddDietFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddDietFragment extends Fragment implements SearchFoodAdapter.SearchFoodAdapterListener, AddFoodAdapter.AddFoodAdapterListener {
    private static final String DIET_NAME = "dietName";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG ="AddDietFrag" ;

    private String mDietName;
    private String mParam2;
    @BindView(R.id.searchEt)
    EditText searchEt;
    @BindView(R.id.searchRv)
    RecyclerView searchRv;
    @BindView(R.id.dietFoodRv)
    RecyclerView dietFoodRv;
    SearchFoodAdapter mSearchFoodAdapter;
    RetrofitInit retrofitInit;
    EdamamModel mEdamamModel;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    List<Food> mDietFood;
    AddFoodAdapter mAddFoodAdapter;
    @BindView(R.id.totalKcalTv)
    TextView totalKcalTv;
    @BindView(R.id.requiredKcalTv)
    TextView requiredKcalTv;

    public AddDietFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dietName diet name .
     * @return A new instance of fragment AddDietFragment.
     */
    public static AddDietFragment newInstance(String dietName) {
        AddDietFragment fragment = new AddDietFragment();
        Bundle args = new Bundle();
        args.putString(DIET_NAME, dietName);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDietName = getArguments().getString(DIET_NAME);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_diet, container, false);
        ButterKnife.bind(this, view);
        requiredKcalTv.setText("You need " + User.currentUser.getNeededCAL() + " Kcal");
        retrofitInit = RetrofitInit.getInstance();
        mDietFood = new ArrayList<Food>();
        mAddFoodAdapter = new AddFoodAdapter(this.getContext(), mDietFood, this);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        dietFoodRv.setLayoutManager(llm);
        dietFoodRv.setAdapter(mAddFoodAdapter);
        dietFoodRv.setAnimation(null);

        dietFoodRv.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                Toast.makeText(AddDietFragment.this.getContext(), "changed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @OnClick(R.id.addDietBt)
    public void addDiet(View view) {

        if (searchRv.getVisibility() == View.GONE)
            searchRv.setVisibility(View.VISIBLE);
        else if (searchRv.getVisibility() == View.VISIBLE)
            searchRv.setVisibility(View.GONE);


    }

    @OnClick(R.id.searchIv)
    public void searchFood(View view) {
        searchRv.setVisibility(View.GONE);
        if (!searchEt.getText().toString().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            mEdamamModel = new EdamamModel();
            mSearchFoodAdapter = new SearchFoodAdapter(this.getContext(), mEdamamModel.getAllFood(), this);
            LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
            LinearLayoutManager horizontalLlm = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);

            searchRv.setLayoutManager(horizontalLlm);
            searchRv.setAdapter(mSearchFoodAdapter);
            retrofitInit.getApiInterface().getParsed(searchEt.getText().toString().trim()).enqueue(new Callback<EdamamModel>() {
                @Override
                public void onResponse(Call<EdamamModel> call, Response<EdamamModel> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.body() != null) {
                        searchRv.setVisibility(View.VISIBLE);
                        mEdamamModel.getAllFood().addAll(response.body().getAllFood());
                        mSearchFoodAdapter.notifyDataSetChanged();


                    }

                }

                @Override
                public void onFailure(Call<EdamamModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    searchRv.setVisibility(View.GONE);


                }
            });
        }

    }

    private Food addedFood;

    @Override
    public void addToDiet(Food food) {
        try {
            addedFood = (Food) food.clone();
            DecimalFormat df = new DecimalFormat("#.##");

            if (mDietFood.contains(addedFood)) {
                int i = mDietFood.indexOf(addedFood);
                mDietFood.get(i).setAmount(mDietFood.get(i).getAmount() + 100);
                mAddFoodAdapter.notifyDataSetChanged();
                double totalKcal = calculateCalories(mDietFood);

                totalKcalTv.setText("This diet is: " + df.format(totalKcal) + " Kcal");

            } else {
                mDietFood.add(addedFood);
                mAddFoodAdapter.notifyDataSetChanged();
                double totalKcal = calculateCalories(mDietFood);
                totalKcalTv.setText("This diet is: " + df.format(totalKcal) + " Kcal");
            }
        } catch (Exception e) {
            Log.i(TAG, "addToDiet: "+e.toString());

        }


    }

    @Override
    public void increaseAmount(int i) {
        double totalKcal = calculateCalories(mDietFood);
        totalKcalTv.setText("This diet is: " + String.valueOf(totalKcal) + " Kcal");


    }

    @Override
    public void decreaseAmount(int i) {
        double totalKcal = calculateCalories(mDietFood);
        totalKcalTv.setText("This diet is: " + String.valueOf(totalKcal) + " Kcal");
    }

    @Override
    public void delete(Food food) {
        mDietFood.remove(food);
        mAddFoodAdapter.notifyDataSetChanged();
        double totalKcal = calculateCalories(mDietFood);
        totalKcalTv.setText("This diet is: " + String.valueOf(totalKcal) + " Kcal");


    }

    public double calculateCalories(List<Food> foodList) {
        double total = 0;
        for (Food f : foodList) {
            double foodCal = (f.getAmount() * f.getNutrients().getEnercKcal()) / 100;
            total += foodCal;
        }
        return total;
    }

    @OnClick(R.id.addDietBt)
    public void addDietToFirestore(View view) {

        if (!mDietFood.isEmpty()) {
            LoadingDialog loadingDialog = new LoadingDialog(this.getActivity());
            loadingDialog.startLoadingDialog();
            String id = DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                    .collection(StaticFields.DIET_COLLECTION).document().getId();
            Diet.currentCreatedDiet = new Diet(id, mDietName, System.currentTimeMillis(), calculateCalories(mDietFood), mDietFood);
            DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                    .collection(StaticFields.DIET_COLLECTION).document(id).set(Diet.currentCreatedDiet).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    loadingDialog.dismissDialog();
                    if (task.isSuccessful()) {

                    }

                }
            });

        }
    }

}
