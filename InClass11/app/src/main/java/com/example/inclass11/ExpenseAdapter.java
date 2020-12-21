package com.example.inclass11;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    Context c;
    ArrayList<Expense> expenses;
    static AdapterActivity mListener;

    public ExpenseAdapter(Context c, ArrayList<Expense> expenses) {
        this.c = c;
        this.expenses = expenses;
        this.mListener = (MainActivity)c;
    }



    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item_list,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        holder.tv_name_list.setText(expenses.get(position).getName().toString());
        holder.tv_amount_list.setText("$"+expenses.get(position).getAmount().toString());
        holder.clickedPosition = position;

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView tv_name_list, tv_amount_list;
        Context context;
        int clickedPosition;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name_list = itemView.findViewById(R.id.tv_name_list);
            tv_amount_list = itemView.findViewById(R.id.tv_amount_list);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.click(clickedPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.longClick(clickedPosition);
            return false;
        }
    }

    public interface AdapterActivity{
        public void click(int position);
        public void longClick(int position);
    }

}
