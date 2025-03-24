package com.example.usuarios2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    double latitude, longitude;
    String city;
    GoogleMap mMap;

    ImageView imageUser, imageFlag;
    TextView nameText, emailText, phoneText, locationText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        imageUser = findViewById(R.id.imageUser);
        imageFlag = findViewById(R.id.imageFlag);
        nameText = findViewById(R.id.textName);
        emailText = findViewById(R.id.textEmail);
        phoneText = findViewById(R.id.textPhone);
        locationText = findViewById(R.id.textLocation);

        // Obtener los datos del intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String fullName = extras.getString("name");
            String email = extras.getString("email");
            String phone = extras.getString("phone");
            String location = extras.getString("location");
            String photoUrl = extras.getString("photo");
            String countryCode = extras.getString("countryCode");

            nameText.setText(fullName);
            emailText.setText(email);
            phoneText.setText(phone);
            locationText.setText(location);

            Glide.with(this).load(photoUrl).into(imageUser);
            Glide.with(this).load("https://flagsapi.com/" + countryCode + "/flat/64.png").into(imageFlag);

            latitude = Double.parseDouble(extras.getString("latitude"));
            longitude = Double.parseDouble(extras.getString("longitude"));
            city = extras.getString("location");
        }

        // Inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Agregar marcador en la ubicación del usuario
        LatLng userLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(userLocation).title(city));

        // Centrar el mapa en la ubicación
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));

        // Mostrar la ciudad en un Toast al hacer clic en el marcador
        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(this, "Ciudad: " + city, Toast.LENGTH_SHORT).show();
            return true; // Para evitar que el marcador haga zoom automáticamente
        });
    }
}

