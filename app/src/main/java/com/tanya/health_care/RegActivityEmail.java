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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GeneratePin;
import com.tanya.health_care.code.GetEmail;
import com.tanya.health_care.code.UserData;
import com.tanya.health_care.dialog.CustomDialog;
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
                try{
                    if (email.getText().toString().isEmpty()) {
                        CustomDialog dialogFragment = new CustomDialog("Пожалуйста, введите почту!", false);
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                    } else if (!GetEmail.isValidEmail(email.getText())) {
                        CustomDialog dialogFragment = new CustomDialog("Пожалуйста, введите корректную почту!", false);
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                    } else if (!userAgree.isChecked()) {
                        CustomDialog dialogFragment = new CustomDialog( "Пожалуйста, примите пользовательское соглашение!", false);
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                    } else {
                        final String userEmail = email.getText().toString().trim();
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean userExists = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    UserData user = snapshot.getValue(UserData.class);
                                    if (user != null && user.getEmail().equals(userEmail)) {
                                        userExists = true;
                                        break;
                                    }
                                }
                                if (userExists) {
                                    Toast.makeText(RegActivityEmail.this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                                } else {
                                    final String pinCode = GeneratePin.generatePinCode();
                                    sendEmail(userEmail, pinCode);
                                    Intent intent = new Intent(RegActivityEmail.this, RegPinActivity.class);
                                    intent.putExtra("userEmail", userEmail);
                                    intent.putExtra("pinCode", pinCode);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(RegActivityEmail.this, "Ошибка при проверке пользователя", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception e) {
                    CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
                    dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
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
        final String username = "healthcaree.mycare@gmail.com";
        final String password = "puws knlb onmh nwqy";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

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
            message.setSubject("Подтверждение почты");

            String emailBody = "<html>\n" +
                    " <body style=\"font-family: Arial, sans-serif;\">\n" +
                    "     <h1 style=\"color: black;\">Подтверждение почты</h1>\n" +
                    "     <p style=\"color: black; font-size: 16px;\">Для продолжения регистрации на Health_Care - Забота о здоровье, введите следующий код подтверждения:</p>\n" +
                    "     <h2 style=\"background-color: #f5f5f5; padding: 10px; border-radius: 5px; color: black;\">" + pinCode + "</h2>\n" +
                    "     <p style=\"color: black; font-size: 16px;\">Спасибо за регистрацию!</p>\n" +
                    "     <p style=\"color: black; font-size: 16px;\">Команда Health_Care - Забота о здоровье</p>\n" +
                    " </body>\n" +
                    " </html>";

            message.setContent(emailBody, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
