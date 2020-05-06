package com.example.longkhan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
    private List<MemberModel> memberList;
    private Context context;

    public MemberAdapter(Context context, List<MemberModel> memberList) {
        this.context=context;
        this.memberList=memberList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_list_item, parent, false);
        return new MemberAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MemberModel trip = memberList.get(position);
        holder.user_name.setText(trip.getUsrname());
        System.out.println("check in"+trip.getAmout_peruser());
        holder.user_amount.setText(trip.getAmout_peruser());
        holder.usr_currency.setText(trip.getUsr_currency());
        Glide.with(context).load(trip.getUrlProfile()).override(70,70).error(R.drawable.user).into(holder.imageUser);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user_name;
        ImageView imageUser;
        TextView user_amount;
        TextView usr_currency;
        MyViewHolder(View view) {
            super(view);
            user_name = view.findViewById(R.id.username);
            imageUser = view.findViewById(R.id.imageUser);
            user_amount = view.findViewById(R.id.usr_amount);
            usr_currency = view.findViewById(R.id.user_currency);
        }
    }
}
