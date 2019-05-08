package com.kutumbita.app.utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kutumbita.app.R;

public class S {

    public static void T(Context c, String s) {

        // Toast.makeText(c, s, Toast.LENGTH_LONG).show();


        LayoutInflater inflater = ((Activity) c).getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) ((Activity) c).findViewById(R.id.toast_layout_root));

        TextView text = layout.findViewById(R.id.text);

        text.setText(s);

        Toast toast = new Toast(c);
        toast.setGravity(Gravity.BOTTOM, Utility.dpFromPx(c, -0), Utility.dpFromPx(c, 230));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void L(String s) {

        Log.i("kutumbita", s);
    }

    public static void L(String tag, String s) {

        Log.i(tag, s);
    }
}
