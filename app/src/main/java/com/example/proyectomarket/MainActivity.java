package com.example.proyectomarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectomarket.databinding.ActivityMainBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String servidor = "http://10.0.2.2/marketapp/";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMainBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_cuentauser, R.id.nav_pedidodetalle,R.id.nav_map)
                .setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Intent i =  getIntent();
        Bundle b = i.getExtras();
        Cliente client = (Cliente) b.getSerializable("cliente");

        DatosUsuario();

        VerificarPedidoPendiente(client.getId());

        SesionNavigationDrawer();
    }

    private void DatosUsuario() {
        NavigationView nv = findViewById(R.id.nav_view);
        View headerView = nv.getHeaderView(0);
        TextView txtTitle = headerView.findViewById(R.id.nav_header_title);
        TextView txtSubtitle = headerView.findViewById(R.id.nav_header_subtitle);

        Intent i =  getIntent();
        Bundle b = i.getExtras();
        Usuario user = (Usuario) b.getSerializable("usuario");

        txtTitle.setText("Bienvenido");
        txtSubtitle.setText(user.getNom());
    }

    private void VerificarPedidoPendiente(int id_cliente) {
        //URL para acceder al archivo php
        String url = servidor + "consultar_pedido_pendiente.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        params.put("id_cliente",id_cliente);

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200) {//Ha procesado exitosamente
                    try {
                        String idPedido = new String(responseBody);
                        idPedido = idPedido.replaceAll("\"", "");
                        Pedido.setId(Integer.parseInt(idPedido));
                        //Toast.makeText(getApplicationContext(),"-[" + Pedido.getId() + "]-",Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"Error de ejecución",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void SesionNavigationDrawer() {
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_cerrarsesion) {
                    // Mostrar cuadro de diálogo de confirmación
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Cerrar Sesión");
                    builder.setMessage("¿Estás seguro de que deseas cerrar la sesión?");
                    builder.setPositiveButton("Cerrar Sesión", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Aquí se realiza el cierre de sesión y la redirección
                            Intent intent = new Intent(getApplicationContext(), IniciarSesion.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancelar", null);
                    builder.show();

                    return true;
                } else {
                    // Otros casos para los elementos del menú
                    DrawerLayout drawer = binding.drawerLayout;
                    drawer.closeDrawer(GravityCompat.START);
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(id);
                    return true;
                }
            }
        });
    }
}
