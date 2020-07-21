package com.longlv.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.longlv.eshop.Model.Users;
import com.longlv.eshop.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowBtn, loginBtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intWidget();

        RememberAccount();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void RememberAccount() {
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if (UserPhoneKey != "" && UserPasswordKey != "") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {

                AllowAccess(UserPhoneKey, UserPasswordKey);

                /* show progressBar*/
                ShowProgressDialog();
            }
        }
    }

    private void ShowProgressDialog() {
        loadingBar.setTitle("Account have already");
        loadingBar.setMessage("Vui lòng chờ...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
    }

    private void AllowAccess(final String phoneNumber, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(phoneNumber).exists()) {
                    Users usersData = dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);

                    if (usersData.getPhone().equals(phoneNumber)) {
                        if (usersData.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            /*Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);*/
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Mật khẩu không đúng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Tài khoản với số " + phoneNumber + " không tồn tại!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void intWidget() {
        joinNowBtn = (Button) findViewById(R.id.main_join_btn);
        loginBtn = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
    }
}