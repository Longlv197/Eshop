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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccoutnBtn;
    private EditText inputName, inputPhoneNumber, inputPassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initWidget();

        createAccoutnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String name = inputName.getText().toString();
        String phoneNumber = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Vui lòng điền đủ tên ...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Vui lòng điền đủ số điện thoại ...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Vui lòng điền đủ mật khẩu ...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Tạo tài khoản");
            loadingBar.setMessage("Vui lòng chờ trong vài phút");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name, phoneNumber, password);

            setInputNull();
        }
    }

    private void setInputNull() {
        inputName.setText("");
        inputPhoneNumber.setText("");
        inputPassword.setText("");
    }

    private void ValidatePhoneNumber(final String name, final String phoneNumber, final String password) {
         final DatabaseReference RootRef;
         RootRef = FirebaseDatabase.getInstance().getReference();

         RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (!(dataSnapshot.child("Users").child(phoneNumber).exists())){
                     HashMap<String, Object> userdataMap = new HashMap<>();
                     userdataMap.put("phone", phoneNumber);
                     userdataMap.put("password", password);
                     userdataMap.put("name", name);

                     RootRef.child("Users").child(phoneNumber).updateChildren(userdataMap)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         Toast.makeText(RegisterActivity.this, "Tài khoản của bạn đã được tạo", Toast.LENGTH_SHORT).show();
                                         loadingBar.dismiss();

                                         Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                         startActivity(intent);
                                     }
                                     else {
                                         loadingBar.dismiss();
                                         Toast.makeText(RegisterActivity.this, "Lỗi: Vui lòng thử lại sau ...", Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             });
                 }
                 else {
                     Toast.makeText(RegisterActivity.this, "Số "+ phoneNumber + " này đã được đăng kí!", Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                     Toast.makeText(RegisterActivity.this, "Vui lòng sử dụng số phone khác để đăng kí", Toast.LENGTH_SHORT).show();

                     Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                     startActivity(intent);
                 }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }

    private void initWidget() {
        createAccoutnBtn = (Button) findViewById(R.id.register_btn);
        inputName = (EditText) findViewById(R.id.register_username_input);
        inputPassword = (EditText) findViewById(R.id.register_password_input);
        inputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);
    }
}