package com.kristian.travelerblog;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private double Latitude;
    private double Longitude;
    private String timeStamp;
    private EditText newText;
    private Long ID;
    public Button btnSave,btnShare;
    private String textShow;
    public CallbackManager callbackManager;
    LoginManager manager;
    private String filePath[];
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Krok 8 – Zdieľanie obsahu -inicializácia balíčka
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //4. krok Galéria fotografií spracovanie udajov kt. obsahoval zámer
        Intent intent = getIntent();
        int position = intent.getExtras().getInt("position");
        String[] filePath = intent.getStringArrayExtra("filepath");
        String[] fileName = intent.getStringArrayExtra("filename");
        this.filePath = filePath;

        //zobrazenie fotografie v komponente ImageView
        ImageView view = (ImageView) findViewById(R.id.detailImage);
        Picasso.with(this).load("file://"+ filePath[position]).fit().centerInside().into(view);

        //Krok 5 – Určovanie polohy zhotovenia fotografie
        // inicializácia komponetu pre zobrazovanie mapy
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // spracovanie údajov zo zámeru
        //Latitude = intent.getDoubleExtra("Latitude", 0);
        //Longitude = intent.getDoubleExtra("Longitude", 0);

        //Krok 6 - SQL databáza
        //získanie údajového repozitáru pre získavanie údajov

        DatabaseHelper helper = new DatabaseHelper(this);
        Cursor cursor = helper.getReadableDatabase().query(DataTable.TableInfo.TABLE_NAME,
                new String[]{"_id", "*"}, DataTable.TableInfo.TABLE_ID, null, null, null, null);

        timeStamp = fileName[position];
        //cyklus na vyhľadávanie rovnakej časovej známky
        //následné získanie súradníc z tabuľky
        cursor.moveToFirst();
        do{
            if (timeStamp.equals(cursor.getString(3))){
                Latitude = cursor.getDouble(cursor.getColumnIndex("Latitude"));
                Longitude = cursor.getDouble(cursor.getColumnIndex("Longitude"));
                ID = cursor.getLong(0);
                textShow = cursor.getString(cursor.getColumnIndex("Text"));
            }
        } while (cursor.moveToNext());

        this.position = position;

         newText = (EditText) findViewById(R.id.edit);
        btnSave = (Button) findViewById(R.id.save_button);
        btnShare = (Button) findViewById(R.id.share_button);
        showText();

    }

    //Krok 5 – Určovanie polohy zhotovenia fotografie
    // vytvorenie inštancie triedy LatLng
    //pridanie značky na mapu na bod so súradnicami
    // priblíženie na konkrétny bod
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng poloha = new LatLng(Latitude, Longitude);
        mMap.addMarker(new MarkerOptions().position(poloha));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(poloha, 12));

    }

    public void saveText(View view){
        String textSave = newText.getText().toString();
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        helper.updateTable(ID, textSave, sqLiteDatabase);
        Toast.makeText(this, "Description inserted",Toast.LENGTH_LONG).show();
        textShow=textSave;
        showText();
    }

    private void showText(){
      TextView viewText = (TextView) findViewById(R.id.edit);
        viewText.setText(this.textShow);
    }

    public void publishImage(View view){
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                sharePhoto();
            }

            @Override
            public void onCancel() {
                System.out.println("Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("Error");
            }
        });
    }

    private void sharePhoto(){
        Bitmap image = BitmapFactory.decodeFile(filePath[position]);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption(this.textShow)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
        Toast.makeText(this,"Posted on Facebook", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
