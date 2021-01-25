package com.heshmat.mydietwatcher.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.FormattingDate;
import com.heshmat.mydietwatcher.LoadingDialog;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.models.Diet;
import com.heshmat.mydietwatcher.models.Food;
import com.heshmat.mydietwatcher.models.TodayPlan;
import com.heshmat.mydietwatcher.models.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddFoodAdapter extends RecyclerView.Adapter<AddFoodAdapter.ViewHolder> {
    Context context;
    List<Food> foods;
    boolean readOnly=false;
    private AddFoodAdapterListener listener;

    public AddFoodAdapter(Context context, List<Food> foods, AddFoodAdapterListener listener) {
        this.context = context;
        this.foods = foods;
        this.listener = listener;
    }
    public AddFoodAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
        readOnly=true;
    }


    @NonNull
    @Override
    public AddFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_food_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFoodAdapter.ViewHolder holder, int position) {
        Food food = foods.get(position);

        if (readOnly){
            holder.rv.setVisibility(View.GONE);
            holder.totalAmount.setVisibility(View.VISIBLE);
            holder.totalAmount.setText(context.getString(R.string.amount,food.getAmount()));
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    addFoodToTodayPlan(food);
                    return false;
                }
            });


        }
        DecimalFormat df = new DecimalFormat("#.##");
        holder.nameTv.setText(food.getLabel());
        holder.proteinTv.setText(df.format(food.totalPro()));
        holder.kcalTv.setText(df.format(food.totalKcal()));
        holder.fatTv.setText(df.format(food.totalFat()));
        Glide.with(context).load(food.getImage()).circleCrop().error(R.drawable.ic_logo).into(holder.foodIv);
        holder.amountTv.setText(df.format(food.getAmount()));
        holder.increaseIv.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        food.setAmount(food.getAmount()+100);
                        holder.amountTv.setText(String.valueOf(food.getAmount()));
                        listener.increaseAmount(position);
                    }
                }
        );
        holder.decreaseIv.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (food.getAmount()!=0) {
                            food.setAmount(food.getAmount()-100);
                            holder.amountTv.setText(String.valueOf(food.getAmount()));
                            listener.decreaseAmount(position);
                        }
                    }
                }
        );
        holder.deleteIv.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.delete(food);
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, proteinTv, kcalTv, fatTv, amountTv,totalAmount;
        ImageView foodIv, increaseIv, decreaseIv, deleteIv;
        RelativeLayout rv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.addFoodnameTv);
            proteinTv = itemView.findViewById(R.id.ProteinaddFoodCardTv);
            kcalTv = itemView.findViewById(R.id.kcalsaddFoodCardTv);
            fatTv = itemView.findViewById(R.id.fataddFoodCardTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            foodIv = itemView.findViewById(R.id.foodCAddCardIv);
            increaseIv = itemView.findViewById(R.id.increaseIv);
            decreaseIv = itemView.findViewById(R.id.decreaseIv);
            deleteIv = itemView.findViewById(R.id.deleteIv);
            totalAmount=itemView.findViewById(R.id.addFoodTotalAmountTv);
            rv=itemView.findViewById(R.id.amountRv);


        }
    }

    public interface AddFoodAdapterListener {
        void increaseAmount(int i);

        void decreaseAmount(int i);

        void delete(Food food);

    }

    public void update(Food foo) {
        Food food= foods.get(foods.indexOf(foo));
        food.setAmount(foo.getAmount()+100);

        foods.remove(foo);
        foods.add(food);
        notifyDataSetChanged();


    }
    private void addFoodToTodayPlan(Food food){
        Activity activity = (Activity) context;

        LoadingDialog loadingDialog=new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();

        DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                .collection(StaticFields.TODAY_PLAN_COLLECTION).
                document(FormattingDate.formattedDate(new Date(System.currentTimeMillis())))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                loadingDialog.dismissDialog();

                if (documentSnapshot.exists()){
                    TodayPlan todayPlan=documentSnapshot.toObject(TodayPlan.class);
                    HashMap<String,Food> eatenToday= todayPlan.getEatenFood();
                    if (eatenToday==null)
                        eatenToday=new HashMap<>();
                    addFoodTpHashMap(food,eatenToday);
                    todayPlan.setEatenFood(eatenToday);
                    DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                            .collection(StaticFields.TODAY_PLAN_COLLECTION).
                            document(FormattingDate.formattedDate(new Date(System.currentTimeMillis())))
                            .set(todayPlan, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    new AlertDialog.Builder(context).setMessage("Food was added successfully to you today plan")
                                            .setPositiveButton("ok",null).show();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });


                }
                else {
                    new AlertDialog.Builder(context).setMessage("You haven't set a plan for today yet")
                            .setPositiveButton("ok",null).show();
                }

            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful())
                    loadingDialog.dismissDialog();
            }
        });
    }
    private void addFoodTpHashMap(Food food,HashMap<String,Food> hashMap){
        if (hashMap.containsKey(food.getFoodid())){
            Food f=hashMap.get(food.getFoodid());
            hashMap.get(food.getFoodid()).setAmount(f.getAmount()+food.getAmount());

        }
        else {
            hashMap.put(food.getFoodid(),food);

        }
    }
}
