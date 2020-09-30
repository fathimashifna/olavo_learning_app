package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PDFViewActivity extends AppCompatActivity
{
    TextView header;
    String chap_name,chap_pdf,pdf_url;
    URL url;
    URI uri;
    Uri newUri;
    PDFView pdfView;
    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 42042;
    String pdfFileName;
    Integer pageNumber = 0;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        header=findViewById(R.id.tv_header_comment);
        pdfView=findViewById(R.id.pdf_view);
        iv_back=findViewById(R.id.iv_back);

        if (getIntent().getExtras()!=null)
        {
          chap_name=getIntent().getStringExtra("chapter_name");
            chap_pdf=getIntent().getStringExtra("chapter_pdf");
        }
        header.setText(chap_name);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });  if (isNetworkConnectionAvailable())
        {
            pdf_url= BaseClass.videoImgURL+chap_pdf;
//        Log.e("Chap pdf",""+chap_pdf);
            try {
                url = new URL(pdf_url);
                uri = null;
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();

            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
          //  Log.e("URL",""+url);


            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            try{
                newUri = Uri.parse(url.toString());
                new RetrievePdfStream().execute(String.valueOf(url));
                progress.dismiss();
            }
            catch (Exception e){
                progress.dismiss();
               // Toast.makeText(this, "Failed to load Url :" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            startActivity(new Intent(PDFViewActivity.this, NoNetworkActivity.class));
            finish();
        }
    }

    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
    class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {
                URL url = new URL(strings[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;

            }
            return inputStream;
        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }

}