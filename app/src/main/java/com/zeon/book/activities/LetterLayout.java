package com.zeon.book.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.zeon.book.R;

public class LetterLayout extends AppCompatActivity {
    private TextInputEditText userName;
    private TextInputEditText message;
    private MaterialButton btnSend;
    private MaterialCheckBox checkBoxDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_layout);
        initViews();
        setupSendButton();
    }

    private void initViews() {
        userName = findViewById(R.id.userName);
        message = findViewById(R.id.message);
        btnSend = findViewById(R.id.btnSend);
        checkBoxDeviceInfo = findViewById(R.id.checkBoxDeviceInfo);
    }

    private void setupSendButton() {
        btnSend.setOnClickListener(v -> {
            if (!validateInput()) {
                return;
            }
            sendEmail();
        });
    }

    private boolean validateInput() {
        String userText = getUserNameText();
        String messageText = getMessageText();

        if (userText.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (messageText.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendEmail() {
        String emailBody = buildEmailBody();
        Intent emailIntent = createEmailIntent(emailBody);

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_email_app)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.no_email_app), Toast.LENGTH_SHORT).show();
        }
    }

    private String buildEmailBody() {
        StringBuilder body = new StringBuilder();

        body.append(getMessageText())
                .append("\n\n");

        if (checkBoxDeviceInfo.isChecked()) {
            appendDeviceInfo(body);
        }

        body.append(getString(R.string.best_regards) + "\n")
                .append(getUserNameText());

        return body.toString();
    }

    private void appendDeviceInfo(StringBuilder body) {
        body.append("=== Device information ===\n")
                .append("OS Version: ").append(System.getProperty("os.version")).append("\n")
                .append("Android Version: ").append(Build.VERSION.RELEASE).append("\n")
                .append("Device: ").append(Build.DEVICE).append("\n")
                .append("Model: ").append(Build.MODEL).append("\n")
                .append("Product: ").append(Build.PRODUCT).append("\n")
                .append("Brand: ").append(Build.BRAND).append("\n")
                .append("Manufacturer: ").append(Build.MANUFACTURER).append("\n")
                .append("Hardware: ").append(Build.HARDWARE).append("\n")
                .append("Serial: ").append(Build.SERIAL).append("\n")
                .append("\n");
    }

    private Intent createEmailIntent(String emailBody) {
        return new Intent(Intent.ACTION_SEND)
                .setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.contact_email)})
                .putExtra(Intent.EXTRA_SUBJECT, getEmailSubject())
                .putExtra(Intent.EXTRA_TEXT, emailBody);
    }

    private String getEmailSubject() {
        return getString(R.string.app_name) + " - " + getString(R.string.email_subject);
    }

    private String getUserNameText() {
        return userName.getText() != null ? userName.getText().toString().trim() : "";
    }

    private String getMessageText() {
        return message.getText() != null ? message.getText().toString().trim() : "";
    }
}