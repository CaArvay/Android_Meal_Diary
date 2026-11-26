package com.example.colearvay_project;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.ViewHolder> {
    private ArrayList<Meal> meals;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // constructor, gets data and a context
    MealListAdapter(Context context, ArrayList<Meal> meals)
    {
        this.mInflater = LayoutInflater.from(context); // will inflate from XML our layout
        this.meals = meals;
    }

    // inflates the layout we created that will show one row of data
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // inflates the layout we created that will show one row of data
        View view = mInflater.inflate(R.layout.meal_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.txtMealAte.setText(meal.mealName);
        holder.txtWhereYouAte.setText(meal.resName);
        holder.txtDateEaten.setText(meal.date);

        // Check the rating of the meal to determine how many stars need to be turned on
        switch(meal.rating)
        {
            case 1:
                holder.imgStar1.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar2.setImageResource(android.R.drawable.btn_star_big_off);
                holder.imgStar3.setImageResource(android.R.drawable.btn_star_big_off);
                holder.imgStar4.setImageResource(android.R.drawable.btn_star_big_off);
                holder.imgStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 2:
                holder.imgStar1.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar2.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar3.setImageResource(android.R.drawable.btn_star_big_off);
                holder.imgStar4.setImageResource(android.R.drawable.btn_star_big_off);
                holder.imgStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 3:
                holder.imgStar1.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar2.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar3.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar4.setImageResource(android.R.drawable.btn_star_big_off);
                holder.imgStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 4:
                holder.imgStar1.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar2.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar3.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar4.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 5:
                holder.imgStar1.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar2.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar3.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar4.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgStar5.setImageResource(android.R.drawable.btn_star_big_on);
                break;
            default:
                break;
        }
    }

    /**
     * Function empties the meals arrayList and then re-adds everything from an updated array.
     * Used in main activity when the meals arrayList changes and needs to be updated
     * @param updatedMeals
     */
    public void refillMealArray(ArrayList<Meal> updatedMeals)
    {
        this.meals.clear();

        for (Meal newMeal : updatedMeals)
        {
            this.meals.add(newMeal);
        }
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return meals.size();
    }

    Meal getItem(int id)
    {
        return meals.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener)
    {
        this.mClickListener = itemClickListener;
    }

    //Parent activity or something else will implement this to perform
    // whatever code it wants when a click occurs
    public interface ItemClickListener
    {
        void onItemClick(View v, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtMealAte;
        TextView txtWhereYouAte;
        TextView txtDateEaten;
        ImageView imgStar1;
        ImageView imgStar2;
        ImageView imgStar3;
        ImageView imgStar4;
        ImageView imgStar5;

        ViewHolder(View itemView) {
            //Declare variables for everything visible on a recycler view item
            super(itemView);
            txtMealAte = itemView.findViewById(R.id.txtMealAte);
            txtWhereYouAte = itemView.findViewById(R.id.txtWhereYouAte);
            txtDateEaten = itemView.findViewById(R.id.txtDateEaten);
            imgStar1 = itemView.findViewById(R.id.imgStar1);
            imgStar2 = itemView.findViewById(R.id.imgStar2);
            imgStar3 = itemView.findViewById(R.id.imgStar3);
            imgStar4 = itemView.findViewById(R.id.imgStar4);
            imgStar5 = itemView.findViewById(R.id.imgStar5);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //This checks -> is there a listener for the adapter? If so, call the code it has
            if(mClickListener != null)
            {
                //getAdapterPosition will be the position of this viewholder's data in the list
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }



}
