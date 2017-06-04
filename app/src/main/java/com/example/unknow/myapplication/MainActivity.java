package com.example.unknow.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NfcAdapter.CreateNdefMessageCallback{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Handler mHandler = new Handler();
    private ListView listView;
    String[] devices = {"NBC0001","NBC0002","NBC0003","NBC0004","NBC0005","NBC0006","NBC0006","NBC0007","NBC0008","NBC0009","NBC0010","NBC0011"};

    public void showMessageInSnackbox(String message){
        Snackbar snackbar = Snackbar.make(mSwipeRefreshLayout,message,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //starting loading page
        Intent loadingIntent = new Intent(MainActivity.this,LoadingPage.class);
        startActivity(loadingIntent);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeMain);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        listView = (ListView) findViewById(R.id.listViewMain);
        startUpView();
        startupNfcReceive();
        checkMessageNfc();
    }
    public void startupNfcReceive(){
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
           showMessageInSnackbox("Sorry this device does not have NFC.");
            return;
        }

        if (!mAdapter.isEnabled()) {
            showMessageInSnackbox("Please enable NFC.");
        }

        mAdapter.setNdefPushMessageCallback(this, this);
    }
    public void startUpView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.item_of_ist,devices);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                //showMessageInSnackbox("Assign the " + textView.getText() + " to new Dogfooder!");
                Intent intent = new Intent(MainActivity.this,AssignPage.class);
                intent.putExtra("barcode",textView.getText().toString());
                startActivity(intent);
            }
        } );
    }

    public void showAlertDialogToAssignDevice(String text){
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setMessage("Assigning to you.");
                        progressDialog.setIndeterminate(false);
                        progressDialog.show();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while(progressDialog.getProgress() < 100){
                                            progressDialog.setProgress( progressDialog.getProgress() + 1);
                                            try {
                                                Thread.sleep(50 - progressDialog.getProgress()/2);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                showMessageInSnackbox("Assign to you.");
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });

                        break;
                    default:
                        // nothing
                        break;
                }
            }
        };

        AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage("Assign this device " + text + " to you?")
                .setTitle("Assign to me")
                .setPositiveButton("Yes",onClickListener)
                .setNegativeButton("No", onClickListener)
                .setCancelable(false)
                .create();

        ad.show();
    }
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                showMessageInSnackbox("Update!");
            }
        },1000);

    }
    //this function dont do nothing
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return null;
    }

    public void  checkMessageNfc(){
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0];
            String barcode = new String(message.getRecords()[0].getPayload());
            showAlertDialogToAssignDevice(barcode);
        }
    }

}
