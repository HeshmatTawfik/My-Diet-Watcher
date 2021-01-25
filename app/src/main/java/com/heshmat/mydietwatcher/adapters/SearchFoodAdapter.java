package com.heshmat.mydietwatcher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.models.Food;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.ViewHolder> {
    Context context;
    List<Food> foods;
    private SearchFoodAdapterListener listener;


    public SearchFoodAdapter(Context context, List<Food> foods,SearchFoodAdapterListener listener) {
        this.context = context;
        this.foods = foods;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_search_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foods.get(position);
        DecimalFormat df = new DecimalFormat("#.##");

        holder.nameTv.setText(food.getLabel());
        holder.proteinTv.setText(df.format(food.getNutrients().getProcnt()));
        holder.kcalTv.setText(df.format(food.getNutrients().getEnercKcal()));
        holder.fatTv.setText(df.format(food.getNutrients().getFat()));
        Glide.with(context).load(food.getImage()).circleCrop().error(R.drawable.ic_logo).into(holder.foodIv);
        holder.addToDietBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.addToDiet(food);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, proteinTv, kcalTv, fatTv;
        ImageView foodIv;
        Button addToDietBt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            proteinTv = itemView.findViewById(R.id.proteinsearchCardTv);
            kcalTv = itemView.findViewById(R.id.kcalsearchCardTv);
            fatTv = itemView.findViewById(R.id.fatsearchCardTv);
            foodIv = itemView.findViewById(R.id.foodSearchCardIv);
            addToDietBt = itemView.findViewById(R.id.addToDietSearchCardBt);


        }
    }
    public interface SearchFoodAdapterListener{
        public void addToDiet(Food food);
    }
}
