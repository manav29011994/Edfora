package com.manav.edfora;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manav on 16/12/17.
 */

public class YourPlaylist extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Myadapter myadapter;
    Context con;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.activity_main);
        findViewById(R.id.playlist).setVisibility(View.GONE);
        findViewById(R.id.history).setVisibility(View.GONE);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setAdapter();
    }

    private void setAdapter() {
        List<Model> model=new ArrayList<>();
        if(UserHistory.getInstance().getPlayList()!=null && UserHistory.getInstance().getPlayList().size()>0) {
            model = UserHistory.getInstance().getPlayList();
            myadapter = new Myadapter(model, con,true);
            recyclerView.setAdapter(myadapter);
        }
        else
        {
            Toast.makeText(con,"PlayList is Empty",Toast.LENGTH_LONG).show();
        }
    }
}
