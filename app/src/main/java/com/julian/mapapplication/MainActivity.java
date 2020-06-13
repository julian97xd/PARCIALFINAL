package com.julian.mapapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = MainActivity.class.getSimpleName();

    SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private FusedLocationProviderClient client;
    ProgressDialog progressDialog;

    private FloatingActionButton btnAdd, btnList;
    EditText edtName, edtCoordillera, edtCountry, edtHeight;
    private RequestQueue requestQueue;

    private ItemsDbHelper db;
    private AlertDialog dialog, dialogList;

    private Item currentItem;
    private ItemsCursorAdapter itemsCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando...");
        initViews();
        initListeners();
    }

    private void initViews(){
        initControllerMap();
        itemsCursorAdapter = new ItemsCursorAdapter(this, null, new ItemsCursorAdapter.Listener() {

            @Override
            public void deleteItem(String itemId) {
                progressDialog.show();
                new DeleteLawyerTask().execute(itemId);
            }
        });

        client = LocationServices.getFusedLocationProviderClient(this);

        btnAdd = findViewById(R.id.btnAdd);
        btnList = findViewById(R.id.show_list);
    }

    private void showAllPoints() {
        googleMap.clear();
        progressDialog.show();
        new ItemsLoadTask().execute();
    }

    private void initControllerMap() {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(this);
    }

    private void initListeners(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddPlace();
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogList();
            }
        });
    }

    private void showDialogList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.list_items, null);
        builder.setView(view);
        dialogList = builder.create();
        final RelativeLayout root = new RelativeLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ListView listView = view.findViewById(R.id.list);
        listView.setAdapter(itemsCursorAdapter);

        dialogList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogList.setContentView(root);
        dialogList.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialogList.show();
    }

    private void showDialogAddPlace() {
        //new RegisterPlaceFragment().show(getSupportFragmentManager(), RegisterPlaceFragment.TAG);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.register_place_fragment, null);
        builder.setView(view);
        dialog = builder.create();
        final RelativeLayout root = new RelativeLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //TODO:initViews
        edtName = view.findViewById(R.id.edt_name);
        edtCoordillera = view.findViewById(R.id.edt_coordillera);
        edtCountry = view.findViewById(R.id.edt_country);
        edtHeight = view.findViewById(R.id.edt_height);

        view.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForm()){
                    //TODO: verificar que por GEOCODING que el objecto exista para almacenarlo...

                    String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+"natural+feature+"+getPathForm()+"&=kjghgjhgjgh5";
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    progressDialog.show();
                    OkHttpClient okHttpClient = new OkHttpClient();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "No se encontró ninguna montaña con estas caracteristicas, Intentelo nuevamente", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                String json = response.body().string();
                                Gson gson = new Gson();
                                Example example = gson.fromJson(json, Example.class);
                                if (!example.getResults().isEmpty()){
                                    Item item = new Item(
                                            edtName.getText().toString(),
                                            edtCoordillera.getText().toString(),
                                            edtCountry.getText().toString(),
                                            edtHeight.getText().toString(),
                                            example.getResult().geometry.location
                                    );
                                    currentItem = item;
                                    new AddEditItemTask().execute(item);
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "No se encontró ninguna montaña con estas caracteristicas, Intentelo nuevamente", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "No se encontró ninguna montaña con estas caracteristicas, Intentelo nuevamente", Toast.LENGTH_LONG).show();
                                Log.e(TAG,response.message());
                            }
                        }
                    });
                }
            }
        });


        // creating the fullscreen dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private String getPathForm(){
        String name = edtName.getText().toString();
        String coordillera = edtCoordillera.getText().toString();
        String country = edtCountry.getText().toString();
        String height = edtHeight.getText().toString();
        return getPath(name)+"+"+getPath(country);
    }

    private String getPath(String s){
        return s.toLowerCase().replace(" ","+");
    }

    private boolean checkForm(){
        if (edtName.getText().toString().isEmpty()){
            edtName.setError("No puede ser vacio");
            return false;
        }
        if (edtCoordillera.getText().toString().isEmpty()){
            edtCoordillera.setError("No puede ser vacio");
            return false;
        }
        if (edtCountry.getText().toString().isEmpty()){
            edtCountry.setError("No puede ser vacio");
            return false;
        }
        if (edtHeight.getText().toString().isEmpty()){
            edtHeight.setError("No puede ser vacio");
            return false;
        }
        return true;
    }

    public void getCurrentLocation() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng =  new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                    .title("Estoy aquí");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                            googleMap.addMarker(markerOptions);
                            }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        showAllPoints();
    }

    private class AddEditItemTask extends AsyncTask<Item, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Item... items) {
            return db.saveLawyer(items[0]) > 0;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            progressDialog.dismiss();
            if (result){
                Toast.makeText(MainActivity.this, "Guardado!", Toast.LENGTH_SHORT).show();
                showAllPoints();
            }
            else
                Toast.makeText(MainActivity.this, "No se ha podido guardar el item.", Toast.LENGTH_SHORT).show();
        }

    }

    private class DeleteLawyerTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... voids) {
            return db.deleteItem(voids[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (dialogList!=null)
                dialogList.dismiss();
            if (integer > 0) {
                Toast.makeText(MainActivity.this, "Item eliminado", Toast.LENGTH_SHORT).show();
                showAllPoints();
            }
        }

    }

    private class ItemsLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return db.getAllLawyers();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            progressDialog.dismiss();
            if (cursor != null && cursor.getCount() > 0) {
                itemsCursorAdapter.swapCursor(cursor);
                setMarketPoint(cursor);
            } else {
                // Mostrar empty state
            }
        }
    }

    private void setMarketPoint(Cursor cursor) {
        ArrayList<Item> items = new ArrayList<>();

        items = new Item().toItems(cursor);

        for (Item item : items){
            Log.e(TAG, String.valueOf(items.size()));
            currentItem = item;
            showItemToMap();
        }
    }

    private void showItemToMap() {

        Item item = currentItem;
        Log.e(TAG, item.getName());
        com.julian.mapapplication.Location location = item.getLocation();
        LatLng latLng =  new LatLng(
                location.getLat(),
                location.getLng()
        );
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title(item.getName());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
        currentItem = null;
    }
}
