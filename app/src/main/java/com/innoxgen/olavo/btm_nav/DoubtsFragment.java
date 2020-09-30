package com.innoxgen.olavo.btm_nav;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.Adapter.RowItem;
import com.innoxgen.olavo.activity.ProfileActivity;
import com.innoxgen.olavo.activity.ProfileResetActivity;
import com.innoxgen.olavo.activity.SplashActivity;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.onurkagan.ksnack_lib.Animations.Fade;
import com.onurkagan.ksnack_lib.KSnack.KSnack;
import com.onurkagan.ksnack_lib.KSnack.KSnackBarEventListener;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class DoubtsFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {

    Spinner class_spinner, subject_spinner;
    String device_id, user_id, question, cls_id, sub_id;
    public static final String MY_PREFS_NAME = "LoginStatus";
    ArrayAdapter cls_spinner, sub_spinner;
    EditText et_question;
    HashMap<String, String> cls_map = new HashMap<String, String>();
    HashMap<String, String> sub_map = new HashMap<String, String>();
    ArrayList<RowItem> clsItems = new ArrayList<>();
    ArrayList<RowItem> subItems = new ArrayList<>();
    ArrayList<String> clsList = new ArrayList<String>();
    ArrayList<String> subList = new ArrayList<String>();
    RowItem item, item1;
    KSnack kSnack;
    Button Submit;
    LinearLayout line_pdf_upload;
    TextView pdf_name;
    String uploadFileName;
    private boolean isViewShown = false;


    //Image request code
    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    //Bitmap to get image from gallery
    private Bitmap bitmap;
    //Uri to store the image uri
    private Uri filePath;
    String path;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discussion_forum, container, false);
        class_spinner = root.findViewById(R.id.class_spinner);
        subject_spinner = root.findViewById(R.id.subject_spinner);
        et_question = root.findViewById(R.id.et_question);
        Submit = root.findViewById(R.id.Submit);
        kSnack = new KSnack((Activity) getContext());
        line_pdf_upload = root.findViewById(R.id.line_pdf_upload);
        pdf_name = root.findViewById(R.id.pdf_name);
        SharedPreferences sharedPref = getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (kSnack != null) {
            kSnack.dismiss();
        }

        class_spinner.setOnItemSelectedListener(this);
        subject_spinner.setOnItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
        if (isNetworkConnectionAvailable()) {
            getList(user_id, device_id);

            Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question = et_question.getText().toString();
                    if (question.length() == 0) {
                        et_question.setError("Please enter your question");
                        et_question.requestFocus();
                    } else {
                        if (path != null) {
                            uploadMultipart(user_id, cls_id, sub_id, question, device_id, path);
                        } else {
                            submitdata(user_id, cls_id, sub_id, question, device_id, "");
                        }


                    }

                }
            });
            line_pdf_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        showFileChooser();
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog

                    }

                }
            });
        } else {
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
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getList(final String user_id, final String device_id) {

        String url1 = BaseClass.mainURL + "get_video";
        //Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        clsItems.clear();
        subItems.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            // Log.e("status",""+response);
                            JSONObject jo = null;
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (status.equals("true")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String course_id = jo.getString("course_id");
                                    String subject_id = jo.getString("subject_id");
                                    String subject_name = jo.getString("subject_name");
                                    String class_name = jo.getString("class_name");

                                    item = new RowItem(class_name, course_id);
                                    item1 = new RowItem(subject_name, subject_id);


                                    if (!clsList.contains(class_name)) {
                                        clsItems.add(item);
                                        clsList.add(class_name);
                                    }

                                    if (!subList.contains(subject_name)) {
                                        subItems.add(item1);
                                        subList.add(subject_name);

                                    }
                                }
                                class_spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, clsList));

                                subject_spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, subList));

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-doubts_alert", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token", device_id);
                params.put("user_id", user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
        if (parent.getId() == R.id.class_spinner) {
            if (clsList.get(position).equals(clsItems.get(position).getTitle())) {
                cls_id = clsItems.get(position).getClsId();
            }
        } else if (parent.getId() == R.id.subject_spinner) {
            if (subList.get(position) == subItems.get(position).getTitle()) {
                sub_id = subItems.get(position).getClsId();
            }
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        if (parent.getId() == R.id.class_spinner) {
            cls_id = clsItems.get(0).getClsId();
        }
        if (parent.getId() == R.id.subject_spinner) {
            sub_id = subItems.get(0).getClsId();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void uploadMultipart(final String user_id, final String cls_id, final String sub_id, final String qstn, final String device_id, final String path) {
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            String url1 = BaseClass.mainURL + "doubts";


            new MultipartUploadRequest(getContext(), uploadId, url1)
                    .addFileToUpload(path, "file") //Adding file
                    .addParameter("course_id", cls_id)
                    .addParameter("user_id", user_id)
                    .addParameter("subject_id", sub_id)
                    .addParameter("question", qstn)
                    .addParameter("device_token", device_id)
                    .setMaxRetries(2)
                    .startUpload();

            //Starting the upload

            kSnack.setListener(new KSnackBarEventListener() {
                @Override
                public void showedSnackBar() {
                    System.out.println("Showed");
                    et_question.setFocusable(false);
                    et_question.setFocusableInTouchMode(false);
                    et_question.getText().clear();
                    pdf_name.setText(R.string.attachment);
                }

                @Override
                public void stoppedSnackBar() {
                    et_question.setFocusable(true);
                    et_question.setFocusableInTouchMode(true);
                    et_question.getText().clear();


                }
            })
                    .setAction("Close", new View.OnClickListener() { // name and clicklistener
                        @Override
                        public void onClick(View v) {
                            // System.out.println("Your action !");
                            kSnack.dismiss();
                            et_question.setFocusable(true);
                            et_question.setFocusableInTouchMode(true);
                            et_question.getText().clear();


                        }
                    })
                    .setMessage("Query Submitted Suceesfully") // message
                    .setTextColor(R.color.white) // message text color
                    .setBackColor(R.color.colorPrimary) // background color
                    .setButtonTextColor(R.color.white) // action button text color
                    .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                    //.setDuration(3000) // for auto close.
                    .show();
        } catch (Exception exc) {
            Log.e("Error", "" + exc.getMessage());
            Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void submitdata(final String user_id, final String cls_id, final String sub_id, final String qstn, final String device_id, final String uploadFileName) {

        String url1 = BaseClass.mainURL + "doubts";
        //Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");

                            kSnack.setListener(new KSnackBarEventListener() {
                                @Override
                                public void showedSnackBar() {
                                    System.out.println("Showed");
                                    et_question.setFocusable(false);
                                    et_question.setFocusableInTouchMode(false);
                                    et_question.getText().clear();
                                }

                                @Override
                                public void stoppedSnackBar() {
                                    et_question.setFocusable(true);
                                    et_question.setFocusableInTouchMode(true);
                                    et_question.getText().clear();

                                }
                            })
                                    .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                        @Override
                                        public void onClick(View v) {
                                            // System.out.println("Your action !");
                                            kSnack.dismiss();
                                            et_question.setFocusable(true);
                                            et_question.setFocusableInTouchMode(true);
                                            et_question.getText().clear();

                                        }
                                    })
                                    .setMessage(data) // message
                                    .setTextColor(R.color.white) // message text color
                                    .setBackColor(R.color.colorPrimary) // background color
                                    .setButtonTextColor(R.color.white) // action button text color
                                    .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                                    //.setDuration(3000) // for auto close.
                                    .show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-doubtsubmit_alert", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("course_id", cls_id);
                params.put("subject_id", sub_id);
                params.put("question", qstn);
                params.put("file", uploadFileName);
                params.put("device_token", device_id);


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String displayName = null;
                    filePath = data.getData();
                    path = getPath(filePath);

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();

                    }

                    pdf_name.setText("" + displayName);

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri) {
        if (uri != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null); // this line throws error
            assert cursor != null;
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getActivity().getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            assert cursor != null;
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();

            return path;
        } else {
            //do log/toast to user
            return "";
        }
    }

}