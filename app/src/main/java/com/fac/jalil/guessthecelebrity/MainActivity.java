package com.fac.jalil.guessthecelebrity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls=new ArrayList<>();
    ArrayList<String> celebNames=new ArrayList<>();
    Button btn1,btn2,btn3,btn4;
    DownloadTask task;
    RelativeLayout rl;
   ImageView imageView;
   Random random;
   int pickedCeleb=0,locationOfCorrectAns=0;
   String []answers=new String[4];
    public class DownloadTask extends AsyncTask<String,Void,String>{
        String result="";
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url=new URL(urls[0]);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream=httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();
                while (data!=-1)
                {
                    char ch=(char)data;
                    result+=ch;
                    data=reader.read();
                }

            } catch (Exception e) {
                e.printStackTrace();
               return null;
            }
            return result;
        }
    }

    public class DownloadImageTask extends AsyncTask<String,Void,Bitmap>
    {
        Bitmap bitmap;
        URL url;
        HttpURLConnection urlConnection;

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                url=new URL(strings[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream stream=urlConnection.getInputStream();
                bitmap= BitmapFactory.decodeStream(stream);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        random= new Random();
        task = new DownloadTask();
        imageView = (ImageView) findViewById(R.id.image);
        String webcontent = null;
        try {
            webcontent = task.execute("http://cdn.posh24.se/kandisar").get();
            String[] splitedWebcontent = webcontent.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcher = p.matcher(splitedWebcontent[0]);
            while (matcher.find()) {
                celebUrls.add(matcher.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            matcher = p.matcher(splitedWebcontent[0]);
            while (matcher.find()) {
                celebNames.add(matcher.group(1));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        referesh();
    }
    public void referesh()
    {
        DownloadImageTask imageTask=new DownloadImageTask();

        pickedCeleb=random.nextInt(celebUrls.size());
        locationOfCorrectAns=random.nextInt(4);
        try {
            Bitmap celebImage=imageTask.execute(celebUrls.get(pickedCeleb)).get();
            imageView.setImageBitmap(celebImage);
            int incorrectAns;
            for(int i=0;i<4;i++)
            {
                if(i==locationOfCorrectAns)
                {
                    answers[i]=celebNames.get(pickedCeleb);
                }
                else
                {
                    incorrectAns=random.nextInt(celebUrls.size());
                    while (incorrectAns==pickedCeleb)
                    {
                        incorrectAns=random.nextInt(celebUrls.size());

                    }
                    answers[i]=celebNames.get(incorrectAns);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        btn1.setText(answers[0]);
        btn2.setText(answers[1]);
        btn3.setText(answers[2]);
        btn4.setText(answers[3]);
    }
    public void celebChosen(View view)
    {
        if(view.getTag().equals(Integer.toString(locationOfCorrectAns)))
        {
            Toast.makeText(this,"You are right!",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this,"Oooops! it was "+celebNames.get(locationOfCorrectAns),Toast.LENGTH_LONG).show();
        }
        referesh();
    }
}
