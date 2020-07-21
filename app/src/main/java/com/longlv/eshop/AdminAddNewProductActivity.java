package com.longlv.eshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, decription, price, pName, saveCurrentDate, saveCurrentTime;
    private Button addNewProductBtn;
    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDecription, inputProductPrice;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImgRef;
    private DatabaseReference productsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        initWidget();
        CategoryName = getIntent().getExtras().get("category").toString();

        //Them anh
        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        // kiem tra thong tin san pham
        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }

    private void ValidateProductData() {
        decription = inputProductDecription.getText().toString();
        price = inputProductPrice.getText().toString();
        pName = inputProductName.getText().toString();

        if (imageUri == null){
            Toast.makeText(this, "Ảnh sản phẩm là bắt buộc...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(decription)){
            Toast.makeText(this, "Vui lòng điển thông tin miêu tả sản phẩm...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Vui lòng điền mục giá sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pName)){
            Toast.makeText(this, "Vui lòng điền mục tên sản phẩm", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("Thêm sản phẩm mới");
        loadingBar.setMessage("Vui lòng chờ chút khi 1 sản phẩm mới đang được thêm");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calender.getTime());

        productRandomKey = saveCurrentDate + " - " + saveCurrentTime;

        final StorageReference filePath = productImgRef.child(imageUri.getPathSegments() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Ảnh đã được tải lên thành công...", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "got the Product image Url is Successfully", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();

                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("time", saveCurrentTime);
        productMap.put("date", saveCurrentDate);
        productMap.put("description", decription);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", price);
        productMap.put("name", pName);

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Sản phẩm đã được thêm thành công", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }

    private void initWidget() {
        addNewProductBtn = (Button) findViewById(R.id.add_new_product);
        inputProductImage = (ImageView) findViewById(R.id.select_product_image);
        inputProductName = (EditText) findViewById(R.id.product_name);
        inputProductDecription = (EditText) findViewById(R.id.product_description);
        inputProductPrice = (EditText) findViewById(R.id.product_price);
        productImgRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);
    }


}