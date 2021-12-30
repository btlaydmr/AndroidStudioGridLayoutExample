package com.nuraydemir.gridlayoutodev;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.nuraydemir.gridlayoutodev.databinding.ActivityMainBinding;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Photo> photoArrayList;
    PhotoAdapter photoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        photoArrayList=new ArrayList<>();
        int numberOfColumns = 3;

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,numberOfColumns));
        photoAdapter = new PhotoAdapter(photoArrayList);
        binding.recyclerView.setAdapter(photoAdapter);

        getData();

    }
   public void getData() {
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Photos",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM photos", null);
            int imageIx = cursor.getColumnIndex("image");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {

                byte[] bytes = cursor.getBlob(imageIx);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    int id = cursor.getInt(idIx);
                    Photo photo = new Photo(id,bitmap);
                    photoArrayList.add(photo);
            }
            photoAdapter.notifyDataSetChanged();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.grid_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_photo){
            //eğer item AddArt seçildiyse ife gir
            Intent intent=new Intent(MainActivity.this,activity_photo.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}