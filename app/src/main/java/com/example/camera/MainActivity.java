package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnTirarFoto = findViewById(R.id.btnTirarFoto);
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Vinculando a Câmera
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Se o dispositivo tem câmera
                if (camera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        final Button btnFechar = findViewById(R.id.btnFechar);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView novaFoto = (ImageView)findViewById(R.id.novaFoto);
            Bundle parametros = data.getExtras();
            Bitmap bitmapFoto = (Bitmap) parametros.get("data");
            novaFoto.setImageBitmap(bitmapFoto);

            // Checando se o APP tem permissão de escrita
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            try {
                // Cria um STREAM e comprime a foto nele
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapFoto.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                // Converte em um Array
                byte[] bytes = stream.toByteArray();

                // Nome do arquivo com PATH completo
                String nomeArquivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() +
                        File.separator + "Camera" + File.separator + "IMG_"+String.valueOf(System.currentTimeMillis()) + ".jpg";

                Toast.makeText(this, nomeArquivo, Toast.LENGTH_LONG).show();

                // Grava o arquivo físico
                FileOutputStream fos = new FileOutputStream(nomeArquivo);
                fos.write(bytes);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}