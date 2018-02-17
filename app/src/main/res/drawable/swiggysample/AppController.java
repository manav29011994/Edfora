package com.example.edfora;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by manav on 18/1/17.
 */


public class AppController {

    private static com.example.manav.swiggysample.AppController appController;
    private RequestQueue requestQueue;
    private static Context mctx;


    private AppController(Context context)
    {
        mctx=context;
        requestQueue=getRequestQueue();
    }

    public  RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized com.example.manav.swiggysample.AppController getInstance(Context context)
    {
        if(appController==null)
        {
            appController=new com.example.manav.swiggysample.AppController(context);
        }
        return appController;
    }
  public <T> void addtoRequestQueue(Request<T> request)
  {
    requestQueue.add(request);
  }
}