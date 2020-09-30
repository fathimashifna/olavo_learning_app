package com.innoxgen.olavo.nav_item;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.innoxgen.olavo.R;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.others.WebViewClientImpl;

import java.net.URI;
import java.net.URL;

public class TermsFragment extends Fragment {

    WebView webview;
    URI uri;
    URL url;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terms, container, false);
        getActivity().setTitle(R.string.Terms);
        webview = root.findViewById(R.id.webview_terms);



        if (isNetworkConnectionAvailable())
        {
            ProgressDialog progress = new ProgressDialog(getContext());
            progress.setMessage("Loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            String url1 = "https://olavo.in/app/rules/Terms_and_Condition.html";
            WebSettings webSettings = webview.getSettings();
            WebViewClientImpl webViewClient = new WebViewClientImpl(getActivity(), progress);
            webview.setWebViewClient(webViewClient);
            webview.loadUrl(String.valueOf(url1));
            webview.setOnKeyListener(new View.OnKeyListener() {

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP
                            && webview.canGoBack()) {
                        handler.sendEmptyMessage(1);
                        return true;
                    }

                    return false;
                }

            });
        }
        else
        {
            Fragment fragment = new NoNetworkFragment();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.addToBackStack(null);
            getActivity().getSupportFragmentManager().popBackStack();
            ft.commit();
        }
        return root;
    }
    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
    private void webViewGoBack(){
        webview.goBack();
    }
}