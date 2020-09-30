package com.innoxgen.olavo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.onurkagan.ksnack_lib.KSnack.KSnack;
import com.onurkagan.ksnack_lib.KSnack.KSnackBarEventListener;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fathima Shifna K on 30-08-2020.
 */
public class ProfileResetActivity extends AppCompatActivity {
    CircleImageView profile_image, img_cam;
    EditText et_name, et_email, et_phone;
    LinearLayout place_order;
    String name, email, phone_no, image, user_id, new_name, new_email, new_mobile, device_id;
    KSnack kSnack;
    ImageView iv_back;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    //Bitmap to get image from gallery
    private Bitmap bitmap;
    //Uri to store the image uri
    private Uri filePath;
    String path;
    Notification.Builder builder;
    private static final String TAG = "AndroidUploadService";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_edit_user_profile);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        place_order = findViewById(R.id.place_order);
        profile_image = findViewById(R.id.profile_image);
        img_cam = findViewById(R.id.img_cam);
        kSnack = new KSnack(ProfileResetActivity.this);
        iv_back = findViewById(R.id.iv_back);
        if (ContextCompat.checkSelfPermission(ProfileResetActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(ProfileResetActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (getIntent().getExtras() != null) {
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            phone_no = getIntent().getStringExtra("mobile");
            image = getIntent().getStringExtra("image");
            user_id = getIntent().getStringExtra("userid");
        }
        et_name.setText(name);
        et_email.setText(email);
        et_phone.setText(phone_no);

        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        if (isNetworkConnectionAvailable()) {

            place_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new_name = et_name.getText().toString();

                    Log.e("path", "" + path);
                    if (path!=null)
                    {
                        Log.e("path2", "" + path);
                        uploadMultipart(user_id, device_id, phone_no, new_name, path);
                    }
                    else
                    {
                        editProfile(user_id, device_id, phone_no, new_name, "");
                    }



                }
            });
            img_cam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profile_image.setImageBitmap(null);
                    showFileChooser();
                    //getting the actual path of the image

                }
            });

            String imgurl = BaseClass.userURL + image;

            Picasso.get()
                    .load(imgurl).fit().centerCrop()
                    .into(profile_image);
        } else {
            startActivity(new Intent(ProfileResetActivity.this, NoNetworkActivity.class));
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

    public void uploadMultipart(final String user_id, final String device_id, final String mobile, final String name, final String path) {
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, uploadId, BaseClass.mainURL + "user_update_profile")
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", name)
                    .addParameter("user_id", user_id)
                    .addParameter("mobile", mobile)
                    .addParameter("name", name)
                    .addParameter("device_token", device_id)
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
                    finish();
            startActivity(new Intent(ProfileResetActivity.this, ProfileActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));

        } catch (Exception exc) {
            Log.e("Error", "" + exc.getMessage());
            Toast.makeText(this, "Error"+exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    //method to show file chooser
    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data.getData();
            path = getPath(filePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPath(Uri uri) {
        if (uri != null) {
            Cursor cursor = ProfileResetActivity.this.getContentResolver().query(uri, null, null, null, null); // this line throws error
            assert cursor != null;
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = ProfileResetActivity.this.getContentResolver().query(
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void editProfile(final String user_id, final String device_id, final String phone_no, final String name, final String imagepath) {

        String url1 = BaseClass.mainURL + "user_update_profile";
//Log.e("url",""+url1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                          //  Log.e("status", "" + response);
                            if (status.equals("true")) {
                                startActivity(new Intent(ProfileResetActivity.this, ProfileActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-profilerst_alert", "" + error.getMessage());
                       // progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", user_id);
                params.put("mobile", phone_no);
                params.put("name", name);
                // params.put("password",passwd);
                params.put("device_token", device_id);
                params.put("image", "");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}


