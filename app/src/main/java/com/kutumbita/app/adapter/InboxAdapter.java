package com.kutumbita.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kutumbita.app.R;
import com.kutumbita.app.model.Inbox;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.TheViewHolder> {

    LayoutInflater inflater;
    List<Inbox> listModel = Collections.EMPTY_LIST;
    Context c;
    View v = null;


    public InboxAdapter(Context c, List<Inbox> listModel) {
        inflater = LayoutInflater.from(c);
        this.c = c;
        this.listModel = listModel;
    }

    @Override
    public TheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = inflater.inflate(R.layout.row_inbox, parent, false);
        TheViewHolder holder = new TheViewHolder(v);
        return holder;

    }


    @Override
    public void onBindViewHolder(TheViewHolder holder, int position) {

        Inbox inbox = listModel.get(position);
        holder.title.setText(inbox.getTitle());
        holder.type.setText(inbox.getMessageType().getTitle());
        holder.date.setText(inbox.getDate());
        Picasso.get().load(inbox.getMessageType().getIcon()).into(holder.img);

    }


    @Override
    public int getItemCount() {
        return listModel.size();
    }


    class TheViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView title, type, date;

        RelativeLayout rl;


        public TheViewHolder(final View itemView) {
            super(itemView);


            rl = itemView.findViewById(R.id.rl);
            img = itemView.findViewById(R.id.ivType);

            type = itemView.findViewById(R.id.tvType);

            title = itemView.findViewById(R.id.tvTitle);
            date = itemView.findViewById(R.id.tvDate);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRecycleViewItemClick(itemView, listModel, getAdapterPosition());
                }
            });

        }
    }

    OnRecycleViewItemClickListener listener;

    public void setOnRecycleViewItemClickListener(OnRecycleViewItemClickListener listener) {

        this.listener = listener;
    }

    public interface OnRecycleViewItemClickListener {

        void onRecycleViewItemClick(View v, List<Inbox> model, int position);
    }

}
