package com.manav.edfora;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by manav on 16/12/17.
 */

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyHistoryviewHolder> {


    List<Model> models;
    private Context context;

    static final Random rand = new Random();
    MyHistoryAdapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }


    @Override
    public MyHistoryviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.savehistory, parent, false);
        MyHistoryviewHolder myviewHolder = new MyHistoryviewHolder(view);
        return myviewHolder;
    }

    @Override
    public void onBindViewHolder(final MyHistoryviewHolder holder, final int position) {
        holder.textview1.setText(models.get(position).getSong());
        holder.textView2.setText("Artists: " + models.get(position).getArtists());

        //using glide to load image into viewholder
        Glide.with(context)
                .load(models.get(position).getCover_image())
                .into(holder.imageView1);

        if(models.get(position).getAction()!=null)
        {
            if(models.get(position).getAction().equals("S"))
            holder.tvAction.setText("Last Played on");
            else if(models.get(position).getAction().equals("D"))
                holder.tvAction.setText("Last Downloaded on");
        }
        if(models.get(position).getTimeStamp()!=null)
        {
            holder.tvtimestamp.setText(models.get(position).getTimeStamp());
        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }



    //view holder class

    class MyHistoryviewHolder extends RecyclerView.ViewHolder {

        ImageView imageView1;
        TextView textview1, textView2,tvAction,tvtimestamp;

        public MyHistoryviewHolder(View itemView) {
            super(itemView);

            imageView1 = (ImageView) itemView.findViewById(R.id.imv1);
            textview1 = (TextView) itemView.findViewById(R.id.tv1);


            Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "SourceSansPro-Semibold.ttf");
            Typeface custom_font1 = Typeface.createFromAsset(context.getAssets(),  "Roboto-Regular.ttf");


            textView2 = (TextView) itemView.findViewById(R.id.tv2);
            textView2.setTypeface(custom_font1);textview1.setTypeface(custom_font);

            tvAction=(TextView) itemView.findViewById(R.id.action);
            tvAction.setTypeface(custom_font1);

            tvtimestamp=(TextView) itemView.findViewById(R.id.timestamp);
            tvAction.setTypeface(custom_font1);
        }
    }


}