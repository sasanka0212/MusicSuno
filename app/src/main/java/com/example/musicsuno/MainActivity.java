package com.example.musicsuno;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //Toast.makeText(MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> myMusics = fetchSongs(Environment.getExternalStorageDirectory());
                        String items[] = new String[myMusics.size()];
                        for(int i = 0; i<myMusics.size(); i++){
                            items[i] = myMusics.get(i).getName().replace(".mp3","");
                        }
                        ArrayAdapter<String> Ad = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(Ad);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(MainActivity.this, PlayMusic.class);
                                String currentSong = listView.getItemAtPosition(i).toString();
                                intent.putExtra("songList", myMusics);
                                intent.putExtra("currentSong", currentSong);
                                intent.putExtra("position", i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> fetchSongs(File file) {
        ArrayList al = new ArrayList();
        File songs[] = file.listFiles();
        if(songs!=null) {
            for (File myFile : songs) {
                if (myFile.isHidden() == false && myFile.isDirectory()) {
                    al.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3") && myFile.getName().startsWith(".")==false){
                        al.add(myFile);
                    }
                }
            }
        }
        return al;
    }
}