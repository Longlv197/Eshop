package com.longlv.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.longlv.eshop.Model.Users;
import com.longlv.eshop.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputNumber, inputPassword;
    private Button loginBtn;
    private ProgressDialog loadingBar;
    private TextView adminLink, notAdminLink;

    private String parentDBName = "Users";
    private CheckBox chkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initWidget();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login As Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);

                parentDBName = "Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Đăng nhập");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);

                parentDBName = "Users";
            }
        });
    }

    private void LoginUser() {
        String phoneNumber = inputNumber.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Vui lòng điền đủ số điện thoại ...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đủ mật khẩu ...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Đang đăng nhập");
            loadingBar.setMessage("Vui lòng chờ trong vài phút");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phoneNumber, password);
        }
    }

    private void AllowAccessToAccount(final String phoneNumber, final String password) {

        if (chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phoneNumber);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDBName).child(phoneNumber).exists()) {
                    Users usersData = dataSnapshot.child(parentDBName).child(phoneNumber).getValue(Users.class);

                    if (usersData.getPhone().equals(phoneNumber)) {
                        if (usersData.getPassword().equals(password)) {
                            if (parentDBName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin, logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDBName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                /*Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);*/
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Mật khẩu không đúng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            inputPassword.setText("");
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản với số " + phoneNumber + " không tồn tại!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initWidget() {
        {
            loginBtn = (Button) findViewById(R.id.login_btn);
            inputNumber = (EditText) findViewById(R.id.login_phone_number_input);
            inputPassword = (EditText) findViewById(R.id.login_password_input);
            loadingBar = new ProgressDialog(this);
            chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chekb);
            Paper.init(this);
            adminLink = (TextView) findViewById(R.id.admin_panel_link);
            notAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        }
    }
}