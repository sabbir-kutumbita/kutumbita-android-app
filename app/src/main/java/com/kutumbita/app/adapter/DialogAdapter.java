package com.kutumbita.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kutumbita.app.R;
import com.kutumbita.app.chat.Dialog;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

public class DialogAdapter extends RecyclerView.Adapter {

    private static final int MESSAGE_TYPE_USER = 1;
    private static final int MESSAGE_TYPE_BOT = 2;
    LayoutInflater inflater;
    Context c;
    List<Dialog> dialogs = Collections.emptyList();
    View rightItemView, leftItemView;
    public MutableLiveData<Boolean> liveData;
    private int lastPosition = -1;
    public DialogAdapter(Context c, List<Dialog> dialogs) {

        inflater = LayoutInflater.from(c);
        this.c = c;
        this.dialogs = dialogs;
        liveData = new MutableLiveData();
        liveData.setValue(false);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


        if (viewType == MESSAGE_TYPE_USER) {


            View v = inflater.inflate(R.layout.row_chat_right, viewGroup, false);
            RightViewHolder viewHolder = new RightViewHolder(v);
            return viewHolder;

        } else if (viewType == MESSAGE_TYPE_BOT) {

            View v = inflater.inflate(R.layout.row_chat_left, viewGroup, false);
            LeftViewHolder viewHolder = new LeftViewHolder(v);
            return viewHolder;

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        Dialog d = dialogs.get(position);

        switch (viewHolder.getItemViewType()) {
            case MESSAGE_TYPE_USER:

                ((RightViewHolder) viewHolder).bind(d,position);
                break;

            case MESSAGE_TYPE_BOT:
                ((LeftViewHolder) viewHolder).bind(d,position);
//                if (d.isEnd()) {
//
//                    rightItemView.findViewById(R.id.ivMenu).setVisibility(View.INVISIBLE);
//                }
                break;
        }


    }


    @Override
    public int getItemViewType(int position) {

        Dialog dialog = dialogs.get(position);

        if (dialog.getSender() == Dialog.SENDER_USER) {

            return MESSAGE_TYPE_USER;
        } else if (dialog.getSender() == Dialog.SENDER_BOT) {

            return MESSAGE_TYPE_BOT;
        }

        return position;
    }

    @Override
    public int getItemCount() {
        return dialogs.size();
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            leftItemView = itemView;
            tv = itemView.findViewById(R.id.tvMessage);

        }

        void bind(Dialog dialog, int pos) {



            tv.setText(dialog.getQuestion());

//            if(dialog.isEnd())
//                rightItemView.findViewById(R.id.ivMenu).setVisibility(View.INVISIBLE);
        }


    }

    class RightViewHolder extends RecyclerView.ViewHolder {


        TextView tv;
        ImageView ivMenu;

        public RightViewHolder(@NonNull View itemView) {


            super(itemView);
            rightItemView = itemView;
            tv = itemView.findViewById(R.id.tvMessage);
            ivMenu = itemView.findViewById(R.id.ivMenu);

        }

        void bind(Dialog dialog, int pos) {

            tv.setText(dialog.getQuestion());
            setAnimation(tv, pos);
            if (!dialog.getType().contentEquals("bot") && !dialog.getType().contentEquals("none")) {
                if (getAdapterPosition() == dialogs.size() - 2)

                    ivMenu.setVisibility(View.VISIBLE);
                else
                    ivMenu.setVisibility(View.INVISIBLE);

            } else {

                ivMenu.setVisibility(View.INVISIBLE);
            }

            ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    liveData.setValue(true);
                }
            });


        }

        void hideMenu() {


            if (getAdapterPosition() == dialogs.size() - 2)
                ivMenu.setVisibility(View.INVISIBLE);
            else
                ivMenu.setVisibility(View.VISIBLE);

        }

    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(c, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
