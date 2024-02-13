package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tanya.health_care.code.GeneratePin;
import com.tanya.health_care.code.getEmail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ResetEmailActivity extends AppCompatActivity {

    private Button btn, bb;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_email);

        btn = findViewById(R.id.continu);
        bb = findViewById(R.id.back);
        email = findViewById(R.id.email);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(ResetEmailActivity.this, "Пожалуйста, введите почту!", Toast.LENGTH_SHORT).show();
                } else if (!getEmail.isValidEmail(email.getText())) {
                    Toast.makeText(ResetEmailActivity.this, "Пожалуйста, введите корректную почту", Toast.LENGTH_SHORT).show();
                } else {
                    final String pinCode = GeneratePin.generatePinCode();
                    sendEmail(email.getText().toString(), pinCode);
                    Intent intent = new Intent(ResetEmailActivity.this, ResetPinActivity.class);
                    startActivity(intent);
                }
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetEmailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    public static void sendEmail(final String toEmail, final String pinCode) {
        AsyncTask<Void, Void, Void> emailTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sendEmailInBackground(toEmail, pinCode);
                return null;
            }
        };

        emailTask.execute();
    }

    private static void sendEmailInBackground(String toEmail, String pinCode) {
        final String username = "ochy.tickets@gmail.com";
        final String password = "ivrcjihrdhacolge";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Восстановление профиля");
            message.setText("Для подтверждения электронной почты используйте пин-код: " + pinCode);

            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}