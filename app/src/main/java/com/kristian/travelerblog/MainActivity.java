package com.kristian.travelerblog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Button button;
    private ImageView view;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private File directory;
    private Uri fileUri;
    private String timeStamp;
    private GridView gridView;
    private GridViewAdapter adapter;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double Latitude;
    private double Longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //1.krok Vytvorenie nového prejektu
        //inicializácia komponentu Button
        button = (Button) findViewById(R.id.act_btn);
        button.setText(R.string.button_name);
        //4. krok Galéria fotografií
        gridView = (GridView) findViewById(R.id.gridView);
        getOutputFile();
        refresh();
        //2.krok zachytenie fotografie
        //inicializácia komponentu ImageView
        //vytvorenie funkcie, kde sa po kliknutí na fotografiu spustí aktivita DetailsActivity
        /*view = (ImageView)findViewById(R.id.photo_view);
        view.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showPhotoDetail = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(showPhotoDetail);
            }
        });*/
        //4. krok Galéria fotografií
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //vytvorenie zameru a pripojenie dolezitych udajov pre spravne zobrazenie
            //fotografie
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("filepath", FilePathStrings);
                intent.putExtra("filename", FileNameStrings);
                intent.putExtra("position", position);
                //5. krok Určovanie polohy zhotovenia fotografie
                //intent.putExtra("Latitude", Latitude);
                //intent.putExtra("Longitude", Longitude);
                startActivity(intent);
            }
        });
        buildGoogleApiClient();

    }


    //Vytvorenie metódy na spracovanie kliknutia na tlačidlo
    public void onButtonClick(View view) {
        //1.krok vytvorenie nového projektu
        //vytvorenie zámeru na spustenie novej aktivity DetailsActivity
        /*Intent newActivity = new Intent(this, DetailsActivity.class);
        startActivity(newActivity);*/
        //2.krok zachytenie fotografie
        //vytvorenie zámeru na spustenie existujúcej aplikácie fotoaparátu
        Intent newPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //3.krok ukladanie fotografií
        //priradenie adresáru s fotkami a priloženie k intentu
        fileUri = Uri.fromFile(getOutputFile());
        newPhoto.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        //spracovanie výsledku pomocou metódy onActivityResult
        if (newPhoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(newPhoto, REQUEST_IMAGE_CAPTURE);
        }

    }

    //3. krok Ukladanie fotografií (Vytvorenie priečinka pre ukladanie fotiek a pridanie časovej známky ku fotkám
    private File getOutputFile() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                return null;
            }
        }
        timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_ss", Locale.GERMAN).format(new Date());
        return new File(directory.getPath() + File.separator + timeStamp);
    }

    //3.krok ukladanie fotografií, vytvorenie metódy pre zobrazovanie uložených fotiek
   /* private void showPhoto(String photoUri){
        File imageFile = new File(photoUri);
        if (imageFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            view.setImageBitmap(bitmap);
        }
    }*/

    //4. krok Galéria fotografií
    private void refresh() {
        if (directory.isDirectory()) {
            File[] listFile = directory.listFiles();
            //triedenie poľa objektov
            Arrays.sort(listFile, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    if (((File) lhs).lastModified() > ((File) rhs).lastModified()) {
                        return -1;
                    } else if (((File) lhs).lastModified() < ((File) rhs).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });
            // vytvorenie zatriedeneho pola retazcov pre cestu k suborom
            FilePathStrings = new String[listFile.length];
            int count = 0;
            for (File i : listFile) {
                FilePathStrings[count] = i.getAbsolutePath();
                count++;
            }

            //vytvorenie zatriedeneho pola nazvov
            FileNameStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++) {
                FileNameStrings[i] = listFile[i].getName();
            }
        }
        //  poslanie pola retazcov do adaptera
        adapter = new GridViewAdapter(this, this, FilePathStrings);
        // nastavenie adaptera do Gridview
        gridView.setAdapter(adapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //2.krok zachytenie fotografie
            //zobrazenie zachytenej fotografie
            // vytvorenie inštancií triedy Bundle a Bitmap a priradenie
            // fotografie do komponentu ImageView
            /*Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            view.setImageBitmap(bitmap);*/
            //3.krok ukladanie fotografií
            //showPhoto(fileUri.getPath());
            //6. SQL databáza
            //vkladanie potrebných údajov do tabuľky
            DatabaseHelper helper = new DatabaseHelper(this);
            helper.insertData(helper, mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(), timeStamp, null);
            //4. krok Galéria fotografií
            refresh();
        }
    }
    // Krok 5 – Určovanie polohy zhotovenia fotografie
    // vytvorenie inštancie pre GoogleApiClient
    // pridanie API z LocationServices
    protected void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    //Krok 5 – Určovanie polohy zhotovenia fotografie
    //pripojenie klienta
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    //Krok 5 – Určovanie polohy zhotovenia fotografie
    //odpojenie klienta
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    //Krok 5 – Určovanie polohy zhotovenia fotografie
    //získanie poslednej známej polohy pomocou klienta Google API
    // získanie zemepísnej šírky a dĺžky
    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        /*if (mLastLocation != null) {
            Latitude = mLastLocation.getLatitude();
            Longitude = mLastLocation.getLongitude();
        }
        Log.d("LatitudeMain", String.valueOf(mLastLocation.getLatitude()));
        Log.d("longitudeMain", String.valueOf(mLastLocation.getLongitude()));*/



    }
    //Krok 5 – Určovanie polohy zhotovenia fotografie
    // výpis o chybe pri zrušenom pripojení
    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("ConnectionSuspended");
    }

    //Krok 5 – Určovanie polohy zhotovenia fotografie
    // výpis o chybe pri pripojení ktoré zlyhalo
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("ConnectionFailed");
    }
}
