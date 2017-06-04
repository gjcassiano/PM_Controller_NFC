package com.example.unknow.myapplication;


import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AssignPage extends Activity implements NfcAdapter.CreateNdefMessageCallback{
    private TextView barcodeSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_page);
        barcodeSelected =(TextView)findViewById(R.id.barcode);
        barcodeSelected.setText(getIntent().getExtras().getString("barcode").toString());

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showMessageInSnackbox(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinateLayout),message,Snackbar.LENGTH_LONG);
        View view  = snackbar.getView();
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        view.setLayoutParams(layoutParams);
        snackbar.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        //create a message to send anther device
        String message = barcodeSelected.getText().toString();
        NdefRecord ndefRecord = NdefRecord.createMime("application/barcode_received", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);

        //create message on screan
        showMessageInSnackbox("Sending " + barcodeSelected + " to another device.");
        return ndefMessage;
    }
}
