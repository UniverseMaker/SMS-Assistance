package com.dspark.smsassistance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ActivityRequest activityRequest;

    public FBRemoteConfig mFBRemoteConfig;

    public int noticeUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityRequest.refreshData();
                Snackbar.make(view, "새로고침 시도", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container, ActivityRequest.newInstance(this, "",""));
        //transaction.addToBackStack(null);
        transaction.commit();

        try{
            DBHelper dbHelper = new DBHelper(this, "SMSASSISTANCE", null , 1);

            //dbHelper.insertRecord("test", "Test", "test");
            //int abb = dbHelper.countRecord();
            //String bbb = "dd" + String.valueOf(abb);
        }catch (Exception e){
            e.printStackTrace();
        }

        FBRemoteConfig.checkGooglePlayServices(this);
        mFBRemoteConfig = new FBRemoteConfig(this);
        mFBRemoteConfig.initialize();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
                int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

                if (permissionCheck == PackageManager.PERMISSION_DENIED ||
                        permissionCheck2 == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setTitle("권한요청");
                    dlgAlert.setMessage("다음 화면에서 온라인기반 서비스를 제공하기 위해 인터넷과 핸드폰상태를 읽을 수 있는 권한을 요청합니다");
                    dlgAlert.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog

                                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS}, 1);
                                }
                            });
                    dlgAlert.setNegativeButton("거절",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog
                                    finish();
                                }
                            });

                    //dlgAlert.create().show();
                    Dialog dlg = dlgAlert.create();
                    dlg.setCanceledOnTouchOutside(false);
                    dlg.show();

                } else {
                    // 권한 있음
                    mFBRemoteConfig.onFirebaseConfigLoad_defaultWork();
                    mFBRemoteConfig.onFirebaseConfigLoad_createInstance();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                try {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // 권한 허가
                        // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                        mFBRemoteConfig.onFirebaseConfigLoad_defaultWork();
                        mFBRemoteConfig.onFirebaseConfigLoad_createInstance();
                    } else {
                        finish();
                    }
                }catch (Exception e){

                }
                return;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_request) {
            // Handle the camera action
        } else if (id == R.id.nav_log) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
