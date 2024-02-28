package com.tanya.health_care;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.tanya.health_care.code.GeneratePin;
import com.tanya.health_care.code.GetEmail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class RegActivityEmail extends AppCompatActivity {

    private Button btn, bb;
    private TextView email;
    private CheckBox userAgree;
    private FirebaseAuth mAuth;
    private String myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_email);

        mAuth = FirebaseAuth.getInstance();

        btn = findViewById(R.id.back);
        bb = findViewById(R.id.continu);
        email = findViewById(R.id.email);
        userAgree = findViewById(R.id.userargee);

        myEmail = getIntent().getStringExtra("userEmail");
        if(myEmail != null)
        {
            email.setText(myEmail);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegActivityEmail.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, введите почту!", Toast.LENGTH_SHORT).show();
                } else if (!GetEmail.isValidEmail(email.getText())) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, введите корректную почту", Toast.LENGTH_SHORT).show();
                } else if (!userAgree.isChecked()) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, примите пользовательское соглашение", Toast.LENGTH_SHORT).show();
                } else {
                    final String userEmail = email.getText().toString().trim();
                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        final String pinCode = GeneratePin.generatePinCode();
                                        sendEmail(email.getText().toString(), pinCode);
                                        Intent intent = new Intent(RegActivityEmail.this, RegPinActivity.class);
                                        intent.putExtra("userEmail", userEmail);
                                        intent.putExtra("pinCode", pinCode);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegActivityEmail.this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
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
            message.setSubject("Подтверждение пин-кода");
            message.setText("Для подтверждения электронной почты используйте пин-код: " + pinCode);

            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
