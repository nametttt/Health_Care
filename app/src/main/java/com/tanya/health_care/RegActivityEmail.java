package com.tanya.health_care;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GeneratePin;
import com.tanya.health_care.code.GetEmail;
import com.tanya.health_care.code.UserData;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RegActivityEmail extends AppCompatActivity {

    private Button btn, bb;
    private EditText email;
    private TextView userAgreement;
    private CheckBox userAgree;
    private String myEmail, myCode, birthday, gender, newEmail;
    private boolean allDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_email);

        init();
    }

    private void init() {
        try {
            btn = findViewById(R.id.back);
            bb = findViewById(R.id.continu);
            email = findViewById(R.id.email);
            userAgree = findViewById(R.id.userargee);
            userAgreement = findViewById(R.id.userAgreement);
            myEmail = getIntent().getStringExtra("userEmail");
            myCode = getIntent().getStringExtra("UserCode");
            birthday = getIntent().getStringExtra("userBirthday");
            gender = getIntent().getStringExtra("userGender");
            if (myEmail != null) {
                email.setText(myEmail);
                newEmail = myEmail;
            }
            if(myCode != null){
                allDone = true;
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RegActivityEmail.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            userAgreement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String userEmail = email.getText().toString().trim();
                    Intent intent = new Intent(RegActivityEmail.this, UserAgreementActivity.class);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("UserCode", myCode);
                    intent.putExtra("userGender", gender);
                    intent.putExtra("userBirthday", birthday);
                    startActivity(intent);
                }
            });

            bb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (email.getText().toString().isEmpty()) {
                            CustomDialog dialogFragment = new CustomDialog("Пожалуйста, введите почту!", false);
                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                        } else if (!GetEmail.isValidEmail(email.getText().toString())) {
                            CustomDialog dialogFragment = new CustomDialog("Пожалуйста, введите корректную почту!", false);
                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                        } else if (!userAgree.isChecked()) {
                            CustomDialog dialogFragment = new CustomDialog("Пожалуйста, примите пользовательское соглашение!", false);
                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                        } else {
                            final String userEmail = email.getText().toString().trim();
                            if (userEmail != null && !userEmail.isEmpty()) {
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean userExists = false;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            UserData user = snapshot.getValue(UserData.class);
                                            if (user != null && user.getEmail() != null && user.getEmail().equals(userEmail)) {
                                                userExists = true;
                                                break;
                                            }
                                        }
                                        if (userExists) {
                                            CustomDialog dialogFragment = new CustomDialog("Пользователь уже существует!", false);
                                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                                        } else {
                                            String pinCode = myCode;
                                            if(!allDone || !Objects.equals(newEmail, userEmail))
                                            {
                                                pinCode = GeneratePin.generatePinCode();
                                                sendEmail(userEmail, pinCode);
                                                gender = null;
                                                birthday = null;
                                                myCode = null;
                                            }
                                            Intent intent = new Intent(RegActivityEmail.this, RegPinActivity.class);
                                            intent.putExtra("userEmail", userEmail);
                                            intent.putExtra("pinCode", pinCode);
                                            intent.putExtra("UserCode", myCode);
                                            intent.putExtra("userGender", gender);
                                            intent.putExtra("userBirthday", birthday);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        CustomDialog dialogFragment = new CustomDialog("Ошибка при проверке пользователя: " + databaseError, false);
                                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                                    }
                                });
                            } else {
                                CustomDialog dialogFragment = new CustomDialog("Пожалуйста, введите корректную почту!", false);
                                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                            }
                        }
                    } catch (Exception e) {
                        CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                    }
                }
            });
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
        }
    }

    public static void sendEmail(final String toEmail, final String pinCode) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                sendEmailInBackground(toEmail, pinCode);
            }
        });
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
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
