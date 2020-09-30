package com.innoxgen.olavo.others;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;



public class WebViewClientImpl extends WebViewClient {

    private Activity activity = null;
    ProgressDialog progressDialog;

    public WebViewClientImpl(Activity activity, ProgressDialog progressDialog) {
        this.activity = activity;
       this.progressDialog=progressDialog;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
       // webView.loadUrl(url);

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        if(url.indexOf(url) > -1 ) return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);

        return true;

      //  }
      /*  if(url.indexOf("http://chanakyabmc.com/app/") > -1 ) return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);*/
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        System.out.println("on finish");
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

}