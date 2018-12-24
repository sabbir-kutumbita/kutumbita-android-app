package com.kutumbita.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kutumbita.app.R;
import com.kutumbita.app.bot.survey.Message;

import java.util.Collections;
import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter {

    private static final int MESSAGE_TYPE_USER = 1;
    private static final int MESSAGE_TYPE_BOT = 2;
    LayoutInflater inflater;
    Context c;
    List<Message> messages = Collections.emptyList();

    public SurveyAdapter(Context c, List<Message> messages) {

        inflater = LayoutInflater.from(c);
        this.c = c;
        this.messages = messages;

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

        Message m = messages.get(position);

        switch (viewHolder.getItemViewType()) {
            case MESSAGE_TYPE_USER:
                ((RightViewHolder) viewHolder).bind(m);
                break;
            case MESSAGE_TYPE_BOT:
                ((LeftViewHolder) viewHolder).bind(m);
        }


    }



    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);

        if (message.getType() == "user") {

            return MESSAGE_TYPE_USER;
        } else if (message.getType() == "bot") {

            return MESSAGE_TYPE_BOT;
        }

        return position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvMessage);

        }

        void bind(Message message) {


            // Format the stored timestamp into a readable String using method.
            tv.setText(message.getMsg());

        }


    }

    class RightViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvMessage);

        }

        void bind(Message message) {


            // Format the stored timestamp into a readable String using method.

            tv.setText(message.getMsg());
        }


    }
}
