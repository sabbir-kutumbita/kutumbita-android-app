package com.kutumbita.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kutumbita.app.R;
import com.kutumbita.app.chat.ChatBot;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.DateUtility;
import com.kutumbita.app.utility.PreferenceUtility;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;


public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.TheViewHolder> {

    LayoutInflater inflater;
    List<ChatBot> listModel = Collections.EMPTY_LIST;
    Context c;
    View v = null;
    public MutableLiveData<ChatBot> inBoxLiveData;
    PreferenceUtility preferenceUtility;

    public ChatBotAdapter(Context c, List<ChatBot> listModel) {
        inflater = LayoutInflater.from(c);
        this.c = c;
        this.listModel = listModel;
        inBoxLiveData = new MutableLiveData<>();
        preferenceUtility = new PreferenceUtility(c);
    }

    @Override
    public TheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = inflater.inflate(R.layout.row_bot, parent, false);
        TheViewHolder holder = new TheViewHolder(v);
        return holder;

    }


    @Override
    public void onBindViewHolder(TheViewHolder holder, int position) {

        ChatBot chatBot = listModel.get(position);

        holder.title.setText(chatBot.getName());
        Picasso.get().load(chatBot.getIcon()).into(holder.img);

    }


    @Override
    public int getItemCount() {
        return listModel.size();
    }


    class TheViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView title;

        RelativeLayout rl;


        public TheViewHolder(final View itemView) {
            super(itemView);


            rl = itemView.findViewById(R.id.rel);
            img = itemView.findViewById(R.id.ivrGrid);
            title = itemView.findViewById(R.id.tvrName);


            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    inBoxLiveData.setValue(listModel.get(getAdapterPosition()));
                }
            });

        }
    }


}
