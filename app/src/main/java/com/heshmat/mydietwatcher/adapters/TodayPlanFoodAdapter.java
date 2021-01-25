package com.heshmat.mydietwatcher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.models.Food;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TodayPlanFoodAdapter extends RecyclerView.Adapter<TodayPlanFoodAdapter.ViewHolder> {
    Context context;
    List<Food> foods;

    public TodayPlanFoodAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_food_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayPlanFoodAdapter.ViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.rv.setVisibility(View.GONE);
        holder.totalAmount.setVisibility(View.VISIBLE);
        holder.totalAmount.setText(context.getString(R.string.amount, food.getAmount()));
        DecimalFormat df = new DecimalFormat("#.##");
        holder.nameTv.setText(food.getLabel());
        holder.proteinTv.setText(df.format(food.totalPro()));
        holder.kcalTv.setText(df.format(food.totalKcal()));
        holder.fatTv.setText(df.format(food.totalFat()));
        Glide.with(context).load(food.getImage()).circleCrop().error(R.drawable.ic_logo).into(holder.foodIv);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, proteinTv, kcalTv, fatTv,totalAmount;
        ImageView foodIv;
        RelativeLayout rv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.addFoodnameTv);
            proteinTv = itemView.findViewById(R.id.ProteinaddFoodCardTv);
            kcalTv = itemView.findViewById(R.id.kcalsaddFoodCardTv);
            fatTv = itemView.findViewById(R.id.fataddFoodCardTv);
            foodIv = itemView.findViewById(R.id.foodCAddCardIv);
            totalAmount=itemView.findViewById(R.id.addFoodTotalAmountTv);
            rv=itemView.findViewById(R.id.amountRv);
        }
    }
}
