package com.heshmat.mydietwatcher.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.adapters.DietAdapter;
import com.heshmat.mydietwatcher.models.Diet;
import com.heshmat.mydietwatcher.models.User;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllMyDietFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllMyDietFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.allDietRv)
    RecyclerView allDietRv;
    DietAdapter dietAdapter;
    Query query;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllMyDietFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllMyDietFragment.
     */
    public static AllMyDietFragment newInstance(String param1, String param2) {
        AllMyDietFragment fragment = new AllMyDietFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static AllMyDietFragment newInstance() {

        return new AllMyDietFragment();
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
        View view=inflater.inflate(R.layout.fragment_all_my_diet, container, false);
        ButterKnife.bind(this,view);
        ((SimpleItemAnimator) Objects.requireNonNull(allDietRv.getItemAnimator())).setSupportsChangeAnimations(false);
        allDietRv.setLayoutManager(
                new LinearLayoutManager(this.getContext()));
        query= DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                .collection(StaticFields.DIET_COLLECTION);
        FirestoreRecyclerOptions<Diet> options
                = new FirestoreRecyclerOptions.Builder<Diet>()
                .setQuery(query, Diet.class)
                .build();
        dietAdapter = new DietAdapter(options, this.getContext());
        allDietRv.setAdapter(dietAdapter);

        return view;
    }
    @OnClick(R.id.fabAddDiet)
    public void addDiet(View view){
        final EditText dietNameEt=new EditText(this.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dietNameEt.setLayoutParams(lp);
        dietNameEt.setHint(getString(R.string.diet_name));
        final AlertDialog dialog = new AlertDialog.Builder(this.getContext())
                .setView(dietNameEt)
                .setTitle(getString(R.string.add_diet))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String dietName=dietNameEt.getText().toString().trim();
                                if (!dietName.isEmpty()){
                                    dietNameEt.setError(null);
                                    getFragmentManager().popBackStack();
                                    dialog.dismiss();
                                    AllMyDietFragment.this.getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentContainer, AddDietFragment.newInstance(dietName)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                            .addToBackStack(null)
                                            .commit();
                                }
                                else {
                                    dietNameEt.setError(getString(R.string.required_field));
                                }
                            }
                        });
                    }
                });
                dialog.show();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (dietAdapter != null)
            dietAdapter.startListening();

    }
    @Override
    public void onStop() {
        super.onStop();
        if (dietAdapter != null)
            dietAdapter.stopListening();


    }

}
