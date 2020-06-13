package com.julian.mapapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterPlaceFragment extends DialogFragment {
    public static final String TAG = RegisterPlaceFragment.class.getSimpleName();

    private Context mContext;
    private View parentView;

    private OkHttpClient client;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =  getActivity();
        client = new OkHttpClient();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final CoordinatorLayout root = new CoordinatorLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }


    private EditText edtName, edtCoordillera, edtCountry, edtHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.register_place_fragment, container, false);
        initViews();
        initListeners();
        return parentView;
    }

    private void initViews(){
        edtName = parentView.findViewById(R.id.edt_name);
        edtCoordillera = parentView.findViewById(R.id.edt_coordillera);
        edtCountry = parentView.findViewById(R.id.edt_country);
        edtHeight = parentView.findViewById(R.id.edt_height);
    }

    private void initListeners(){
        parentView.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForm()){
                    //TODO: verificar que por GEOCODING que el objecto exista para almacenarlo...

                    String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+getPathForm()+"&key=AIzaSyA1mYnKMkhSYz44RntH2AG2QMbeWVP8Nl8";
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    progressDialog.show();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            progressDialog.dismiss();
                            if (response.isSuccessful()){
                                String JsonResponse = response.body().string();
                                Log.e(TAG, JsonResponse);
                            }else {
                                Log.e(TAG, response.message());
                            }
                        }
                    });
                }
            }
        });
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
}
