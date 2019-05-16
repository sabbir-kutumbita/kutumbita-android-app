package com.kutumbita.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.kutumbita.app.R;
import com.kutumbita.app.chat.Dialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;


public class DialogAdapter extends RecyclerView.Adapter {

    LayoutInflater inflater;
    Context c;
    List<Dialog> dialogs = Collections.emptyList();
    View rightItemView, leftItemView, photoItemView;
    public MutableLiveData<Boolean> liveData;
    public MutableLiveData<Boolean> isEnd;

    private int lastPosition = -1;


    public DialogAdapter(Context c, List<Dialog> dialogs) {

        inflater = LayoutInflater.from(c);
        this.c = c;
        this.dialogs = dialogs;
        liveData = new MutableLiveData();
        isEnd = new MutableLiveData<>();

        liveData.setValue(false);
        isEnd.setValue(false);

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


        if (viewType == Dialog.SENDER_USER) {


            View v = inflater.inflate(R.layout.row_chat_right, viewGroup, false);
            RightViewHolder viewHolder = new RightViewHolder(v);
            return viewHolder;

        } else if (viewType == Dialog.SENDER_BOT) {

            View v = inflater.inflate(R.layout.row_chat_left, viewGroup, false);
            LeftViewHolder viewHolder = new LeftViewHolder(v);
            return viewHolder;

        } else if (viewType == Dialog.SENDER_USER_WITH_PHOTO) {

            View v = inflater.inflate(R.layout.row_chat_photo, viewGroup, false);
            PhotoViewHolder viewHolder = new PhotoViewHolder(v);
            return viewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        Dialog d = dialogs.get(position);

        switch (viewHolder.getItemViewType()) {
            case Dialog.SENDER_USER:
                ((RightViewHolder) viewHolder).bind(d);
                break;

            case Dialog.SENDER_BOT:
                ((LeftViewHolder) viewHolder).bind(d);
                isEnd.setValue(d.isEnd());
                break;

            case Dialog.SENDER_USER_WITH_PHOTO:
                ((PhotoViewHolder) viewHolder).bind(d);
                isEnd.setValue(d.isEnd());

                break;
        }


    }


    @Override
    public int getItemViewType(int position) {


        return dialogs.get(position).getSender();

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

        void bind(Dialog dialog) {


            tv.setText(dialog.getQuestionOrPhotoPath());

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

        void bind(Dialog dialog) {

            tv.setText(dialog.getQuestionOrPhotoPath());
            // setAnimation(tv, pos, MESSAGE_TYPE_USER);
            if (!dialog.getType().contentEquals("bot") && !dialog.getType().contentEquals("none")) {
                if (getAdapterPosition() == dialogs.size() - 2) {

                    ivMenu.setVisibility(View.VISIBLE);

                } else
                    ivMenu.setVisibility(View.INVISIBLE);

            } else {

                ivMenu.setVisibility(View.INVISIBLE);
            }

            isEnd.observe((LifecycleOwner) c, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean)
                        ivMenu.setVisibility(View.INVISIBLE);
                }
            });

            ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    liveData.setValue(true);
                }
            });


        }

    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {


        ImageView ivImage, ivMenu;

        public PhotoViewHolder(@NonNull View itemView) {


            super(itemView);
            photoItemView = itemView;
            ivImage = itemView.findViewById(R.id.ivMessage);
            ivMenu = itemView.findViewById(R.id.ivMenu);

        }

        void bind(final Dialog dialog) {

            ivImage.post(new Runnable() {
                @Override
                public void run() {


                    Picasso.get().load(dialog.getQuestionOrPhotoPath()).placeholder(R.drawable.ic_insert_photo_black_24dp).error(R.drawable.ic_error_black_24dp).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            if (bitmap.getHeight() > bitmap.getWidth()) {
                                ivImage.getLayoutParams().height = (int) c.getResources().getDimension(R.dimen.two_hundred_dp);
                                ivImage.getLayoutParams().width = (int) c.getResources().getDimension(R.dimen.one_sixty_dp);

                            } else {

                                ivImage.getLayoutParams().height = (int) c.getResources().getDimension(R.dimen.one_sixty_dp);
                                ivImage.getLayoutParams().width = (int) c.getResources().getDimension(R.dimen.two_hundred_dp);
                            }

                            ivImage.setImageBitmap(bitmap);

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            ivImage.setImageDrawable(errorDrawable);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                            ivImage.setImageDrawable(placeHolderDrawable);
                        }
                    });


                }


            });


            if (!dialog.getType().contentEquals("bot") && !dialog.getType().contentEquals("none")) {
                if (getAdapterPosition() == dialogs.size() - 2) {

                    ivMenu.setVisibility(View.VISIBLE);

                } else
                    ivMenu.setVisibility(View.INVISIBLE);

            } else {

                ivMenu.setVisibility(View.INVISIBLE);
            }

            isEnd.observe((LifecycleOwner) c, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean)
                        ivMenu.setVisibility(View.INVISIBLE);
                }
            });

            ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    liveData.setValue(true);
                }
            });


        }
    }


}
