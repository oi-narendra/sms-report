package com.narendra.smsreport;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText phoneNumber;
    EditText message;
    Button sendButton;
    TextView status;

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    @Override
    protected void onPause() {
        super.onPause();

        try {
            unregisterReceiver(sentReceiver);
            unregisterReceiver(deliveryReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(sentReceiver);
            unregisterReceiver(deliveryReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = findViewById(R.id.et_phone_number);
        message = findViewById(R.id.et_message);
        sendButton = findViewById(R.id.btn_send_sms);
        status = findViewById(R.id.tv_status);

        sendButton.setOnClickListener(v -> {
            String phoneNumberText = phoneNumber.getText().toString();
            String messageText = message.getText().toString();

            if (phoneNumberText.isEmpty() || messageText.isEmpty()) {
                Toast.makeText(this, "Please enter phone number and message", Toast.LENGTH_SHORT).show();
                return;
            }

            sendSMS(phoneNumberText, messageText);
        });

    }

    private void sendSMS(String phoneNumber, String message) {
        status.setText("Sending...");
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        registerReceiver(sentReceiver, new IntentFilter(SENT));
        registerReceiver(deliveryReceiver, new IntentFilter(DELIVERED));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);


    }

    private final BroadcastReceiver deliveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    status.setText("Message delivered");
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    status.setText("Message not delivered");
                    Toast.makeText(context, "SMS not delivered",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }


    };

    private final BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    status.setText("Message sent");
                    Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    status.setText("Generic failure");
                    Toast.makeText(context, "Generic failure",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    status.setText("No service");
                    Toast.makeText(context, "No service",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    status.setText("Null PDU");
                    Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    status.setText("Radio off");
                    Toast.makeText(context, "Radio off",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        }


    };
}
