package com.example.proyectomarket.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectomarket.R;
import com.example.proyectomarket.Usuario;
import com.example.proyectomarket.databinding.FragmentCuentauserBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class CuentaUserFragment extends Fragment {

    EditText nomU,codU,nom,ape,cel,email,dis,dir;
    int id_usuario;
    String nombre_usuario;
    String clave_usuario;

    final String servidor = "http://10.0.2.2/marketapp/";

    private FragmentCuentauserBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CuentaUserViewModel slideshowViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CuentaUserViewModel.class);
        binding = FragmentCuentauserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nomU = (EditText) root.findViewById(R.id.edNombreUserC);
        nomU.setFocusable(false);
        codU = (EditText) root.findViewById(R.id.edClaveUserC);
        codU.setFocusable(false);

        nom = (EditText) root.findViewById(R.id.edNombreC);
        nom.setFocusable(false);
        ape = (EditText) root.findViewById(R.id.edApellidoC);
        ape.setFocusable(false);
        cel = (EditText) root.findViewById(R.id.edCelularC);
        cel.setFocusable(false);
        email = (EditText) root.findViewById(R.id.edEmailC);
        email.setFocusable(false);
        dis = (EditText) root.findViewById(R.id.edDistritoC);
        dis.setFocusable(false);
        dir = (EditText) root.findViewById(R.id.edDireccionC);
        dir.setFocusable(false);

        Bundle b = getActivity().getIntent().getExtras();
        Usuario user = (Usuario) b.getSerializable("usuario");
        id_usuario = user.getId();
        nombre_usuario = user.getNom();
        clave_usuario = user.getCod();
        //Toast.makeText(getContext (),"El id es: " + id_usuario,Toast.LENGTH_LONG).show();

        ConsultarCliente(id_usuario);
        //final TextView textView = binding.textCuentauser;
        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void ConsultarCliente(int id_usuario) {
        //URL para acceder al archivo php
        String url = servidor + "consultar_cliente.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        params.put("id",id_usuario);

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){ //Ha procesado exitosamente
                    try {
                        JSONArray js = new JSONArray(new String(responseBody));
                        String idN="",nomN="",apeN="",celN="",emailN="",dirN="",disN="";

                        for(int i=0; i < js.length();i++){
                            idN=String.valueOf(js.getJSONObject(i).getInt("id_cliente"));
                            nomN=js.getJSONObject(i).getString("nom_cliente");
                            apeN=js.getJSONObject(i).getString("ape_cliente");
                            celN=js.getJSONObject(i).getString("cel_cliente");
                            emailN=js.getJSONObject(i).getString("email_cliente");
                            disN=js.getJSONObject(i).getString("distrito_cliente");
                            dirN=js.getJSONObject(i).getString("dir_cliente");
                        }
                        nomU.setText(nombre_usuario);
                        codU.setText(clave_usuario);

                        nom.setText(nomN);
                        ape.setText(apeN);
                        cel.setText(celN);
                        email.setText(emailN);
                        dis.setText(disN);
                        dir.setText(dirN);
                        Toast.makeText(requireContext(),"Datos cargados correctamente",Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(requireContext(),"Error al cargar los datos",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}