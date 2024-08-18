package com.example.proyectomarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class RegistroUsuario extends AppCompatActivity implements View.OnClickListener{

    EditText nomU,codU,nom,ape,cel,email,distrito,direccion;
    Button registrar;
    final String servidor = "http://10.0.2.2/marketapp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        nomU = (EditText) findViewById(R.id.edNombreUserC2);
        codU = (EditText) findViewById(R.id.edClaveUserC2);

        nom = (EditText) findViewById(R.id.edNombreC2);
        ape = (EditText) findViewById(R.id.edApellidoC2);
        cel = (EditText) findViewById(R.id.edCelularC2);
        email = (EditText) findViewById(R.id.edEmailC2);
        distrito = (EditText) findViewById(R.id.edDistritoC2);
        direccion = (EditText) findViewById(R.id.edDireccionC2);

        registrar = (Button) findViewById(R.id.btnRegistro);
        registrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == registrar){  //Cuando presiones el boton registrar
            String nomU_ = nomU.getText().toString();
            String codU_ = codU.getText().toString();

            String nom_ = nom.getText().toString();
            String ape_ = ape.getText().toString();
            String cel_ = cel.getText().toString();
            String email_ = email.getText().toString();
            String dist_ = distrito.getText().toString();
            String dir_ = direccion.getText().toString();
            if(nomU_.equals("") || codU_.equals("") || nom_.equals("") || ape_.equals("") || cel_.equals("") || email_.equals("") || dist_.equals("") || dir_.equals("")){ //Si los campos estan vacios
                Toast.makeText(getApplicationContext(),"Complete todos los campos",Toast.LENGTH_LONG).show();
            }
            else{   //Si los campos estan completos
                RegistrarCliente(nomU_, codU_, nom_, ape_, cel_, email_, dist_, dir_);
            }
        }
    }

    private void RegistrarCliente(String nomU_, String codU_, String nom_, String ape_, String cel_, String email_, String dist_, String dir_) {
        //URL para acceder al archivo php
        String url = servidor + "registrar_cliente.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        params.put("nomU",nomU_);
        params.put("codU",codU_);

        params.put("nom",nom_);
        params.put("ape",ape_);
        params.put("cel",cel_);
        params.put("email",email_);
        params.put("dist",dist_);
        params.put("dir",dir_);

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){    //Ha procesado exitosamente
                    String res = new String(responseBody);
                    if(res.equals("SI")){
                        Toast.makeText(getApplicationContext(),"Registrado correctamente",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), IniciarSesion.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al registrar",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"Error de conexión",Toast.LENGTH_LONG).show();
            }
        });
    }
}