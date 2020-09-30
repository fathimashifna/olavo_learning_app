package com.innoxgen.olavo.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.internal.NavigationMenu;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.nav_item.LeaderBoardFragment;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.others.CustomDialogClass;
import com.innoxgen.olavo.btm_nav.DoubtsTabFragment;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.nav_item.AboutFragment;
import com.innoxgen.olavo.nav_item.HomeFragment;
import com.innoxgen.olavo.nav_item.MyMembershipListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.innoxgen.olavo.nav_item.PurchaseHistoryActivity;
import com.innoxgen.olavo.nav_item.TermsFragment;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    String name, img;
    TextView user_name;
    CircleImageView profile_img;
    DrawerLayout drawers;
    NavigationMenu navigationMenu;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        user_name = hView.findViewById(R.id.user_name);
        profile_img = hView.findViewById(R.id.imageView);

        if (getIntent().getExtras() != null) {
            name = getIntent().getStringExtra("name");
            img = getIntent().getStringExtra("image");

        }
        user_name.setText(name);

        BottomNavigationView btm_navigation = findViewById(R.id.bottom_nav_view);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) btm_navigation.getChildAt(0);
        btm_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about_us, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        drawers = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationMenu = (NavigationMenu) navigationView.getMenu();
        if (isNetworkConnectionAvailable()) {
            displaySelectedScreen(R.layout.fragment_home);
            String img_url = BaseClass.userURL + img;
            Picasso.get()
                    .load(img_url).fit().centerCrop()
                    .into(profile_img);
        } else {
            Fragment fragment = new NoNetworkFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStack();
            ft.commit();
        }
    }


    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    private void displaySelectedScreen(int itemId) {
        DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
        androidx.fragment.app.Fragment fragment = null;
        switch (itemId) {
            case R.id.nav_share:
                drawer1.closeDrawer(GravityCompat.START);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey! Try olavo learning app for improve life. You can download it from here on Playstore: https://play.google.com/store/apps/details?id=com.innoxgen.olavo";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.nav_home:
                drawer1.closeDrawer(GravityCompat.START);
                Fragment content_fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                toolbar.setTitle("Home");
                getSupportFragmentManager().popBackStack();
                if (!(content_fragment instanceof HomeFragment)) {
                    FrameLayout fl = FrameLayout.class.cast(findViewById(R.id.nav_host_fragment));
                    fl.removeAllViews();
                    fragment = new HomeFragment();
                }

                break;
            case R.id.Course_Package_List:
                drawer1.closeDrawer(GravityCompat.START);
                fragment = new MyMembershipListFragment();
                toolbar.setTitle("My Subscription");
                getSupportFragmentManager().popBackStack();
                break;
            case R.id.subs_plan:
                drawer1.closeDrawer(GravityCompat.START);
                startActivity(new Intent(NavigationActivity.this, PurchaseHistoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.terms_condition:
                drawer1.closeDrawer(GravityCompat.START);
                fragment = new TermsFragment();
                getSupportFragmentManager().popBackStack();
                toolbar.setTitle(R.string.Terms);
                break;
            case R.id.nav_about_us:
                drawer1.closeDrawer(GravityCompat.START);
                fragment = new AboutFragment();
                getSupportFragmentManager().popBackStack();
                toolbar.setTitle(R.string.menu_about);
                break;
            case R.id.nav_leaderboard:
                drawer1.closeDrawer(GravityCompat.START);
                fragment = new LeaderBoardFragment();
                getSupportFragmentManager().popBackStack();
                toolbar.setTitle(R.string.leaderboard);
                break;
            case R.id.nav_logout:
                drawer1.closeDrawer(GravityCompat.START);
                CustomDialogClass cdd = new CustomDialogClass(NavigationActivity.this);
                cdd.show();
                break;
        }
        if (fragment != null) {
            FrameLayout fl = FrameLayout.class.cast(findViewById(R.id.nav_host_fragment));
            fl.removeAllViews();
            androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.nav_host_fragment, fragment);
            getSupportFragmentManager().popBackStack();
            ft.addToBackStack(null);
            ft.commit();


        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment newFragment;
            //FragmentTransaction transaction = getFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_home:
                    //Fragment fragment;
                    Fragment content_fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    toolbar.setTitle("Home");
                    if (!(content_fragment instanceof HomeFragment)) {
                        FrameLayout fl = FrameLayout.class.cast(findViewById(R.id.nav_host_fragment));
                        fl.removeAllViews();
                        content_fragment = new HomeFragment();
                        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.nav_host_fragment, content_fragment);
                        getSupportFragmentManager().popBackStack();
                        ft.addToBackStack(null);
                        ft.commit();
                    }


                    return true;
                case R.id.nav_doubts:
                    // startActivity(new Intent(NavigationActivity.this, DoubtsActivity.class));
                    newFragment = new DoubtsTabFragment();
                    loadFragment(newFragment);
                    toolbar.setTitle("Doubts");
                    return true;
             /*   case R.id.nav_helpline:
                    newFragment = new HelpLineFragment();
                    loadFragment(newFragment);
                    toolbar.setTitle("Help Line");
                    return true;*/

                case R.id.nav_profile:
                    startActivity(new Intent(NavigationActivity.this, ProfileActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                  /*  newFragment = new ProfileFragment();
                    loadFragment(newFragment);
                    toolbar.setTitle("Profile");*/
                    return true;

            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FrameLayout fl = FrameLayout.class.cast(findViewById(R.id.nav_host_fragment));
        fl.removeAllViews();
        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.nav_host_fragment, fragment);
        getSupportFragmentManager().popBackStack();
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment content_fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            // Log.e("content",""+content_fragment);
            if (content_fragment instanceof HomeFragment) {
                finish();
            } else {
                FrameLayout fl = FrameLayout.class.cast(findViewById(R.id.nav_host_fragment));
                fl.removeAllViews();
                Fragment fragment1 = new HomeFragment();
                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.add(R.id.nav_host_fragment, fragment1);
                toolbar.setTitle("Home");
                getSupportFragmentManager().popBackStack();
                ft1.addToBackStack(null);
                ft1.commit();
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}