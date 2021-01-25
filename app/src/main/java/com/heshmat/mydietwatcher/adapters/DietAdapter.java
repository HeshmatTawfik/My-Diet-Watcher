package com.heshmat.mydietwatcher.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.FormattingDate;
import com.heshmat.mydietwatcher.LoadingDialog;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.fragments.AddDietFragment;
import com.heshmat.mydietwatcher.fragments.DietDetailsFragment;
import com.heshmat.mydietwatcher.models.Diet;
import com.heshmat.mydietwatcher.models.TodayPlan;
import com.heshmat.mydietwatcher.models.User;

import java.util.Date;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class DietAdapter extends FirestoreRecyclerAdapter<Diet, DietAdapter.DietViewHolder> {


    Context context;

    public DietAdapter(@NonNull FirestoreRecyclerOptions<Diet> options,Context context) {
        super(options);
        this.context=context;
    }


    @Override
    protected void onBindViewHolder(@NonNull DietViewHolder holder, int position, @NonNull Diet model) {
                holder.nameTv.setText(model.getName());
                holder.caloriesTv.setText(context.getString(R.string.calories,model.getTotalCalories()));
                holder.createdAtTv.setText(FormattingDate.formattedDate(new Date(model.getCreatedAt())));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Diet.currentCreatedDiet=model;

                        AppCompatActivity activity=(AppCompatActivity ) context;
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, DietDetailsFragment.newInstance()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        setTodayPlan( model);
                        return false;
                    }
                });
    }

    @NonNull
    @Override
    public DietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_layout, parent, false);
        return new DietViewHolder(view);
    }
    public class DietViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv,caloriesTv,createdAtTv;
        public DietViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.dietNameTv);
            caloriesTv=itemView.findViewById(R.id.dietTotalCalTv);
            createdAtTv=itemView.findViewById(R.id.dietCreatedAtTv);

        }
    }
    @Override
    public void onDataChanged() {
        if (getItemCount() == 0) {
            new AlertDialog.Builder(context).setMessage(context.getString(R.string.no_diets))
                    .setPositiveButton(context.getText(R.string.ok), null).show();
        }
        super.onDataChanged();
    }
    private void setTodayPlan(Diet diet){
        Activity activity = (Activity) context;

        LoadingDialog loadingDialog=new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();
        String today=FormattingDate.formattedDate(new Date(System.currentTimeMillis()));
        DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                .collection(StaticFields.TODAY_PLAN_COLLECTION).document(today).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    TodayPlan todayPlan=new TodayPlan(today,diet);
                    DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                            .collection(StaticFields.TODAY_PLAN_COLLECTION).document(today)
                            .set(todayPlan).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            new AlertDialog.Builder(context).setMessage("Today plan has been set successfully")
                                    .setPositiveButton("Ok",null)
                                    .show();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.dismissDialog();
                        }
                    });

                }
                else {
                    new AlertDialog.Builder(context).setMessage("You have already set today plan")
                            .setPositiveButton("Ok",null).show();

                }

            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.isSuccessful());
                loadingDialog.dismissDialog();
            }
        });
    }

}
