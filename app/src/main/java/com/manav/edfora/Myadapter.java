package com.manav.edfora;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;
import static java.security.AccessController.getContext;

/**
 * Created by manav on 16/12/17.
 */

//custom adapter
public class Myadapter extends RecyclerView.Adapter<Myadapter.MyviewHolder> {


    List<Model> models;
    private Context context;
    boolean flag;
    boolean isPlaying = false;
    MediaPlayer mediaplayer;

    static final Random rand = new Random();
    Myadapter(List<Model> models, Context context,boolean flag) {
        this.models = models;
        this.context = context;
        this.flag=flag;
    }


    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        MyviewHolder myviewHolder = new MyviewHolder(view);
        return myviewHolder;
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, final int position) {
        holder.textview1.setText(models.get(position).getSong());
        holder.textView2.setText("Artists: " + models.get(position).getArtists());

        //using glide to load image into viewholder
        Glide.with(context)
                .load(models.get(position).getCover_image())
                .into(holder.imageView1);

        //clicking of icon starts straming of aution
        holder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPlaying) {
                    holder.imageButton1.setImageResource(R.drawable.play_play);
                    mediaplayer.stop();
                    Toast.makeText(view.getContext(),"song streaming stopped",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(),"wait while song is streaming and tap again stop playing ",Toast.LENGTH_LONG).show();
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    UserHistory.getInstance().saveSongsHistory(models.get(position),currentDateTimeString,"S");
                    holder.imageButton1.setImageResource(R.drawable.play_stop);
                    mediaplayer = new MediaPlayer();
                    mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaplayer.setDataSource(models.get(position).getUrl());
                        mediaplayer.prepare();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mediaplayer.start();
                }
                isPlaying = !isPlaying;
            }
        });


        //download icon strarts Async task for dowload

        //Note that download songs will be in download folder

        holder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadFile().execute(models.get(position).getUrl());
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                UserHistory.getInstance().saveSongsHistory(models.get(position),currentDateTimeString,"D");

            }
        });

        if(!flag) {
            if (UserHistory.getInstance().songPresentInDb(models.get(position).getUrl()))
                holder.imageButton3.setImageResource(R.drawable.c_follow);
            else
                holder.imageButton3.setImageResource(R.drawable.c_unfollow);
        }
        else {
            holder.imageButton3.setVisibility(GONE);
        }



        holder.imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserHistory.getInstance().songPresentInDb(models.get(position).getUrl()))
                {
                    UserHistory.getInstance().removeFromPlayList(models.get(position).getUrl());
                    holder.imageButton3.setImageResource(R.drawable.c_unfollow);
                    Toast.makeText(context,"Removed from PlayList",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    UserHistory.getInstance().savePlayList(models.get(position));
                    holder.imageButton3.setImageResource(R.drawable.c_follow);
                    Toast.makeText(context,"Added to PlayList",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }



   //view holder class

    class MyviewHolder extends RecyclerView.ViewHolder {

        ImageView imageView1;
        TextView textview1, textView2;
        ImageView imageButton1, imageButton2, imageButton3;

        public MyviewHolder(View itemView) {
            super(itemView);

            imageView1 = (ImageView) itemView.findViewById(R.id.imv1);
            textview1 = (TextView) itemView.findViewById(R.id.tv1);


            Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "SourceSansPro-Semibold.ttf");
            Typeface custom_font1 = Typeface.createFromAsset(context.getAssets(),  "Roboto-Regular.ttf");

            textview1.setTypeface(custom_font);
            textView2 = (TextView) itemView.findViewById(R.id.tv2);
            textView2.setTypeface(custom_font1);
            imageButton1 = (ImageView) itemView.findViewById(R.id.imv2);
            imageButton2 = (ImageView) itemView.findViewById(R.id.imv3);
            imageButton3=(ImageView) itemView.findViewById(R.id.addtoplaylist);
        }
    }

   // Asysnc task for downloading music
    private class DownloadFile extends AsyncTask<String, Integer, String> {

       ProgressDialog pd;

       private PowerManager.WakeLock mWakeLock;
        @Override
        protected String doInBackground(String... urls) {

          //  Log.i("DownloadTask","Constructor done");

       int count;
            try {

                //converting tiny url to actual url

                String s=urls[0];
                URL small = new URL(s);
                HttpURLConnection httpURLConnection = (HttpURLConnection) small.openConnection(Proxy.NO_PROXY);

                // stop following browser redirect
                httpURLConnection.setInstanceFollowRedirects(false);

                // extract location header containing the actual destination URL
                String expandedURL = httpURLConnection.getHeaderField("Location");
                httpURLConnection.disconnect();

                //expanded url contains the actual url
                URL url=new URL(expandedURL);
                URLConnection conection = url.openConnection();
                conection.connect();
                // Get file length
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 10 * 1024);

                //this is for appending a random number with downloaded song for unique id
                int randInt=rand.nextInt(100000)+1;

              //  OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/song-"+randInt+".mp3");
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() +"/Download"+ "/song-"+randInt+".mp3");

                byte data[] = new byte[1024];
                long total = 0;

                //writting to file
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // Write data to file
                    if (lenghtOfFile > 0) // only if total length is known
                        publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();
                // Close streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }


        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
           // showDialog(progress_bar_type);
          /*  PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();*/
            pd=new ProgressDialog(context);
            pd.setMessage("download started");
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(true);

            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });

            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // Dismiss the dialog of progress bar file was downloaded
            Log.i("DownloadTask", "Work Done! PostExecute");
           // mWakeLock.release();
            pd.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File Downloaded", Toast.LENGTH_SHORT).show();

        }
        protected void onProgressUpdate(Integer... progress) {
            // Set progress percentage
            pd.setIndeterminate(false);
            pd.setMax(100);
            pd.setProgress(progress[0]);
        }

    }

    public void setFilter(ArrayList<Model> al) {
        models= new ArrayList<Model>();
        models.addAll(al);
        notifyDataSetChanged();
    }

}
