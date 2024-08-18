package com.example.proyectomarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectomarket.ui.home.HomeFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class IniciarSesion extends AppCompatActivity implements View.OnClickListener {

    EditText nom,clave;
    Button ingresar,registrarse;

    ArrayList id_array_cl = new ArrayList();
    ArrayList id_array_user_cl = new ArrayList();

    ArrayList id_array = new ArrayList();
    ArrayList nom_array = new ArrayList();
    ArrayList clave_array = new ArrayList();

    Usuario user = null;
    Cliente cliente = null;

    final String servidor = "http://10.0.2.2/marketapp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        nom = (EditText) findViewById(R.id.edNombreUser);
        clave = (EditText) findViewById(R.id.edClaveUser);

        ingresar = (Button) findViewById(R.id.btnLogin);
        ingresar.setOnClickListener(this);
        registrarse = (Button) findViewById(R.id.btnSign);
        registrarse.setOnClickListener(this);

        user = new Usuario();
        cliente = new Cliente();

        CargarUsuarios();
        CargarClientes();
    }

    public void CargarUsuarios() {
        //Limpiar los datos de los productos
        id_array.clear();
        nom_array.clear();
        clave_array.clear();

        //URL para acceder al archivo php
        String url = servidor + "listar_usuario.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                if(statusCode==200) //Ha procesado exitosamente
                {
                    try {
                        JSONArray js = new JSONArray(new String(responseBody));

                        for (int i=0; i < js.length();i++)
                        {
                            id_array.add(js.getJSONObject(i).getInt("id_usuario"));
                            nom_array.add(js.getJSONObject(i).getString("nombre_usuario"));
                            clave_array.add(js.getJSONObject(i).getString("clave_usuario"));
                        }
                        // Toast.makeText(getApplicationContext(),String.valueOf(js),Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Toast.makeText(getApplicationContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void CargarClientes() {
        //URL para acceder al archivo php
        String url = servidor + "listar_cliente.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){ //Ha procesado exitosamente
                    try {
                        JSONArray js = new JSONArray(new String(responseBody));
                        for(int i=0; i < js.length();i++){
                            id_array_cl.add(js.getJSONObject(i).getInt("id_cliente"));
                            id_array_user_cl.add(js.getJSONObject(i).getInt("id_usuario"));
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error al cargar los datos",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean VerificarUsuario(String nom_, String clave_) {
        // Recorrer los arreglos
        for (int i = 0; i < nom_array.size(); i++) {
            // Obtenemos el nombre de usuario y la clave
            String nombreUsuario = (String) nom_array.get(i);
            String claveUsuario = (String) clave_array.get(i);
            // Comparar los valores con los proporcionados
            if (nombreUsuario.equals(nom_) && claveUsuario.equals(clave_)) {
                // Usuario encontrado
                user.setId((Integer) id_array.get(i));
                user.setNom((String) nom_array.get(i));
                user.setCod((String) clave_array.get(i));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view == ingresar){  //Cuando presiones el boton ingresar
            String nom_ = nom.getText().toString();
            String clave_ = clave.getText().toString();

            if(nom_.equals("") || clave_.equals("")){
                //Si los campos estan vacios
                Toast.makeText(getApplicationContext(),"Complete todos los campos",Toast.LENGTH_LONG).show();
            }
            else{   //Si los campos estan completos
                if(VerificarUsuario(nom_,clave_)){
                    for (int i = 0; i < id_array_cl.size(); i++) {
                        int idUsuario = (int) id_array_user_cl.get(i);
                        if (idUsuario == user.getId()) {
                            // Cliente encontrado
                            cliente.setId((int)id_array_cl.get(i));
                            break;
                        }
                    }
                    Bundle b = new Bundle();
                    b.putSerializable("usuario",user);
                    b.putSerializable("cliente",cliente);
                    Intent intentMain= new Intent(this, MainActivity.class);
                    intentMain.putExtras(b);
                    startActivity(intentMain);
                    Toast.makeText(getApplicationContext(),"Acceso concedido" ,Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Usuario/Clave no encontrados",Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (view == registrarse){//Cuando presiones el boton registrarse
            Intent intentReg= new Intent(this, RegistroUsuario.class);
            startActivity(intentReg);
        }
    }


}