package com.example.longkhan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Cost_ShowMember extends RecyclerView.Adapter<Cost_ShowMember.MyViewHolder>  {
    private List<MemberModel> memberList;
    private Context context;
    static  List<String> selecmemberList = new ArrayList<>() ;

    public Cost_ShowMember(Context context, List<MemberModel> memberList) {
        this.context=context;
        this.memberList=memberList;
    }

    @NonNull
    @Override
    public Cost_ShowMember.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cost_member_list_item, parent, false);
        return new Cost_ShowMember.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        selecmemberList = new ArrayList<>() ;
        final MemberModel trip = memberList.get(position);
        holder.user_name.setText(trip.getUsrname());
        System.out.println("check in"+trip.getAmout_peruser());
        Glide.with(context).load(trip.getUrlProfile()).override(70,70).error(R.drawable.user).into(holder.imageUser);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user_name;
        ImageView imageUser;
        MyViewHolder(View view) {
            super(view);
            user_name = view.findViewById(R.id.text_usrname);
            imageUser = view.findViewById(R.id.imageUser);
        }
    }
}
