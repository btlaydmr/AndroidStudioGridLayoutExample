package com.nuraydemir.gridlayoutodev;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.nuraydemir.gridlayoutodev.databinding.ActivityPhotoBinding;

import java.io.ByteArrayOutputStream;

public class activity_photo extends AppCompatActivity {
    private ActivityPhotoBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher; //galeriya gitmek için
    ActivityResultLauncher<String> permissionResultLauncher; //izni istemek için
    Bitmap selectedImage;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        database = this.openOrCreateDatabase("Photos", MODE_PRIVATE, null);//databasei initualize ettik her yerde kullanabiliriz




    }

    public void selectedPhoto(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else {
                //request permission
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else {
            //else giriliyosa izin verilmiş,galeriye burada gidicez
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //uri istiyo benden. Uri:adres belirten bir metrik ,biz şu klasörün içine gidiyoruz klasörün içine gidiyoruz gibi
            //ACTION_PICK: tutup almak gibi bir şey
            //MediaStore.Images.Media.EXTERNAL_CONTENT_URI kodun bu kısmında kalerinin yolunu belirtiyoruz
            activityResultLauncher.launch(intentToGallery);
        }

    }
    public void save(View view){

        Bitmap smallImage=makeSmallerImage(selectedImage,300);
        //bu resmi benim amacım birler ve sıfırlara dönüştürmek
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50,outputStream);
        byte[] byteArray=outputStream.toByteArray();
        //SQLite 1 ve 0 şeklinde kaydolacak görseli aldık
        try {

            //data base'i initualize ettik
            database.execSQL("CREATE TABLE IF NOT EXISTS photos (id INTEGER PRIMARY KEY,image BLOB) ");
            //burayı yaparken çok dikkatli olmalıyız çünkü bu kısımda yaptığımız bir hata app'i çökertecektir baştan her şeyi yapmak gerekebilir.
            String sqlString = "INSERT INTO photos (image) VALUES (?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            // SQLiteStatement: sonradan bağlanma işlemlerini,binding işlemlerini yapmamıza olanak sağlıyor
            //75. satırdaki stringi alıp çalıştıracağım ben demek

            sqLiteStatement.bindBlob(1,byteArray);
            sqLiteStatement.execute();
            //bunu çalıştıracak

        } catch (Exception e){
            e.printStackTrace();
        }

        //kayıt olduktan sonra main activitye geri dönmek istiyoruz onun için bir Intent yapıcaz
        // finish(); yazabiliriz bu şekilde açıkda kalan tüm activityleri kapatır ve mainactivity'e geri döner
        Intent intent=new Intent(activity_photo.this,MainActivity.class);
        //aşağıda yeni açtığım activiteyi çalıştır
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
    public Bitmap makeSmallerImage(Bitmap image,int maxSize){
        //SQlite da sıkıntı olmasın diye resmi küçülttüm
        int width=image.getWidth();
        //güncel görselimin genişliğini labilirim
        int height=image.getHeight();

        float bitmapRatio=(float) width/ (float)height;
        if (bitmapRatio>1){
            //lanscape image yatay görsel
            width=maxSize;
            height= (int)(width/bitmapRatio);
        }
        else {
            //portraid image
            height=maxSize;
            width= (int)(height/bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,false);
        //
    }
    private void registerLauncher(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            //StartActivityForResult(): yeni bir activity başlatıyorum ama bir sonuç için
            @Override
            public void onActivityResult(ActivityResult result) {
                //kullanıcı galeriye gittikten sonra vazgeçmiş olabilir,telefonu kapanmış olabilir
                if (result.getResultCode()==RESULT_OK){
                    //RESULT_OK ise kullanıcı galeriden bir şey seçmiş demektir
                    Intent intentFromResult=result.getData();
                    //buradaki getdata bize intenti veriyo
                    //aldığımız intent boş olabilir kontrol etmemizde fayda var
                    if (intentFromResult!=null){
                        //geriye veri döndüyse
                        Uri imageData= intentFromResult.getData();
                        //burada seçtiğim getData() uri'ı veriyo yani kullanıcının seçtiği belgenin nerede kayıtlı olduğunu veriyor
                        //bu image data ile kullanıcının seçtiği datanın nerde olduğunu biliyoruz
                        // bunu bilmek yetmiyo kullanıcının seçtiği şeyi nerde gösteririz o bize kalmş
                        //  binding.selectImage.setImageURI();
                        // setImageURI() ile zaten uri'ı (imageData) kullanarak kullanıcının seçtiği şeyi burda gösterebiliriz
                        //ama u her zaman işimizi görmez çünkü uri ile mi yol gösterdik her zman bu şekilde yapmıyıcaz
                        //bu aldığımız veriyi nasıl bitmape çeviririz ona bakalım
                        //try dene exception yakala,yazdıklarımı dene sıkıntı çıkarsa yakala.Uygulamayı çökertebilecek bir sıkıntı
                        //burada bir sorun çıkabilir çünkü sd kartta kayıtlıdır çıkartılırsa okumayabilir o görsel yerini görsele çevirme işlemi %100 değil

                        try {
                            if(Build.VERSION.SDK_INT>28) {
                                //telefon 28 ve üstü ise
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageData);
                                //biz bi kaynak veriyoruz ordaki veriyi bul resme çevir diyebiliriz.
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.selectedPhoto.setImageBitmap(selectedImage);

                            } else{
                                //28 ve altında bunu kullanıyoruz
                                selectedImage=MediaStore.Images.Media.getBitmap(activity_photo.this.getContentResolver(),imageData);
                                binding.selectedPhoto.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e){
                            //tüm hata mesajını logcat'de görürüz
                            e.printStackTrace();

                        }
                    }
                }
            }
        });
        permissionResultLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //permission granted: izin verildiyse galeriye git
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else{
                    Toast.makeText(activity_photo.this,"permission needeed!",Toast.LENGTH_LONG).show();
                }


            }
        });
    }
}
