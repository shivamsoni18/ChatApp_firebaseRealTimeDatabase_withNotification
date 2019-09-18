package com.example.firebasechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.firebasechat.Activities.ChatActivity;
import com.example.firebasechat.ModelClass.LoginPojo;
import com.example.firebasechat.R;
import com.example.firebasechat.Utils.Temp_UserDetails;

import java.util.ArrayList;

public class RegisteredUserAdapter extends RecyclerView.Adapter<RegisteredUserAdapter.ViewHolder> {

    public RegisteredUserAdapter(ArrayList<LoginPojo> userLIST, Context context) {
        this.userLIST = userLIST;
        this.context = context;
    }

    private ArrayList<LoginPojo> userLIST;
    Context context;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chat_userlist_raw, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final LoginPojo user = userLIST.get(i);

        viewHolder.Uname.setText(user.getUserName());

        TextDrawable drawable = TextDrawable.builder()
                .buildRect(String.valueOf(user.getName().toUpperCase().charAt(0)), Color.LTGRAY);
        viewHolder.imageCircle.setImageDrawable(drawable);

        viewHolder.Uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Temp_UserDetails.OtherUserId = user.getUserName();
                Intent i = new Intent(context, ChatActivity.class);

                i.putExtra("OtherUserid", user.getUserName());
                i.putExtra("OtherName", user.getUserName());

                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userLIST.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Uname;
        ImageView imageCircle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Uname = (TextView) itemView.findViewById(R.id.Uname);
            imageCircle = (ImageView) itemView.findViewById(R.id.imageCircle);

        }
    }
}
