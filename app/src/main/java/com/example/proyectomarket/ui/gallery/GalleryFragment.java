package com.example.proyectomarket.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyectomarket.Cliente;
import com.example.proyectomarket.Pedido;
import com.example.proyectomarket.R;
import com.example.proyectomarket.databinding.FragmentGalleryBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class GalleryFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    ListView lista;
    int id_pedido,id_cliente, contador =0;
    Button finPedido;

    ArrayList id_array = new ArrayList();
    ArrayList nom_array = new ArrayList();
    ArrayList desc_array = new ArrayList();
    ArrayList tipo_array = new ArrayList();
    ArrayList prec_array = new ArrayList();
    ArrayList cant_array = new ArrayList();
    ArrayList total_array = new ArrayList();

    Spinner mdp,comp;
    static String id_mdp="",id_comp="";

    //Array del medio pago
    ArrayList id_array_mdp = new ArrayList();
    ArrayList nom_array_mdp = new ArrayList();

    //Array del comprobante
    ArrayList id_array_comp = new ArrayList();
    ArrayList nom_array_comp = new ArrayList();

    final String servidor = "http://10.0.2.2/marketapp/";

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lista = (ListView) root.findViewById(R.id.lstCarrito);
        lista.setOnItemClickListener(this);

        //Botones
        finPedido = (Button) root.findViewById(R.id.btnCompletar);
        finPedido.setOnClickListener(this);

        //Spinners
        mdp = (Spinner) root.findViewById(R.id.spMedioPago);
        mdp.setOnItemSelectedListener(this);

        comp = (Spinner) root.findViewById(R.id.spComprobante);
        comp.setOnItemSelectedListener(this);

        //Obtener los id almacenados
        /*SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyObject", Context.MODE_PRIVATE);
        String pedidoJson = sharedPreferences.getString("pedido", null);
        Pedido pedido = new Gson().fromJson(pedidoJson, Pedido.class); // Convierte el JSON a objeto usando Gson
        */
        id_pedido = Pedido.getId();

        Bundle b = getActivity().getIntent().getExtras();
        Cliente cliente = (Cliente) b.getSerializable("cliente");
        id_cliente = cliente.getId();

        ConsultarCarrito(id_pedido,id_cliente);
        ListarMedioPago();
        ListarTipoComprobante();

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void ConsultarCarrito(int id_pedido, int id_cliente) {
        //Limpiar los datos de los productos
        id_array.clear();
        nom_array.clear();
        desc_array.clear();
        prec_array.clear();
        tipo_array.clear();
        cant_array.clear();
        total_array.clear();
        //URL para acceder al archivo php

        String url = servidor + "consultar_carrito_pedido.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        params.put("id_pedido",id_pedido);
        params.put("id_cliente",id_cliente);

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
                            contador++;
                            id_array.add(String.valueOf(js.getJSONObject(i).getInt("id_producto")));
                            nom_array.add(js.getJSONObject(i).getString("nom_producto"));
                            desc_array.add(js.getJSONObject(i).getString("desc_producto"));
                            prec_array.add(String.valueOf(js.getJSONObject(i).getDouble("prec_producto")));
                            tipo_array.add(js.getJSONObject(i).getString("tip_producto"));
                            cant_array.add(String.valueOf(js.getJSONObject(i).getInt("cantidad")));
                            total_array.add(String.valueOf(js.getJSONObject(i).getDouble("total")));
                        }
                        //Toast.makeText(requireContext(),String.valueOf(js),Toast.LENGTH_LONG).show();
                        lista.setAdapter(null);
                        ListViewAdapterCarrito lva = new ListViewAdapterCarrito(requireContext());
                        lista.setAdapter(lva);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(requireContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Toast.makeText(requireContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ListViewAdapterCarrito extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        TextView txt_id;
        TextView txt_nom;
        TextView txt_desc;
        TextView txt_prec;
        TextView txt_tipo;
        TextView txt_cant;
        TextView txt_total;
        public ListViewAdapterCarrito(Context context)
        {
            this.context = context;
        }

        @Override
        public int getCount() {
            return nom_array.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_carrito_pedido,null);

            txt_id = (TextView) itemView.findViewById(R.id.txtIdP2);
            txt_nom = (TextView) itemView.findViewById(R.id.txtNombreP2);
            txt_desc = (TextView) itemView.findViewById(R.id.txtDescripcionP2);
            txt_prec = (TextView) itemView.findViewById(R.id.txtPrecioP2);
            txt_tipo = (TextView) itemView.findViewById(R.id.txtTipoP2);
            txt_cant = (TextView) itemView.findViewById(R.id.txtCantidad);
            txt_total = (TextView) itemView.findViewById(R.id.txtTotal);

            //Mostrar informaciòn en los Textview
            txt_id.setText(id_array.get(i).toString());
            txt_nom.setText(nom_array.get(i).toString());
            txt_desc.setText("Descripción: "+ desc_array.get(i).toString());
            txt_prec.setText("Precio: S/." + prec_array.get(i).toString());
            txt_tipo.setText("Tipo: "+ tipo_array.get(i).toString());
            txt_cant.setText("Cantidad: "+ cant_array.get(i).toString());
            txt_total.setText("Total: S/."+total_array.get(i).toString());

            return itemView;
        }
    }

    public void ListarMedioPago() {

        //Limpiar los datos de los productos
        id_array_mdp.clear();
        nom_array_mdp.clear();

        //URL para acceder al archivo php
        String url = servidor + "listar_mdpago.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        //params.put("id",1);

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url,params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){//Ha procesado exitosamente
                    try {
                        JSONArray js = new JSONArray(new String(responseBody));
                        id_array_mdp.add("");
                        nom_array_mdp.add("Seleccione");

                        for (int i=0; i < js.length();i++) {
                            id_array_mdp.add(String.valueOf(js.getJSONObject(i).getInt("id_mdpago")));
                            nom_array_mdp.add(js.getJSONObject(i).getString("nom_mdpago"));
                        }
                        //Toast.makeText(getApplicationContext(),String.valueOf(js),Toast.LENGTH_LONG).show();
                        mdp.setAdapter(null);
                        ListViewAdapterMedioPago lva = new ListViewAdapterMedioPago(requireContext());
                        mdp.setAdapter(lva);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(requireContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                Toast.makeText(requireContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ListViewAdapterMedioPago extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        TextView txt_id;
        TextView txt_nom;

        public ListViewAdapterMedioPago(Context context)
        {
            this.context = context;
        }

        @Override
        public int getCount() {
            return nom_array_mdp.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_spinner_mdp,null);

            txt_id = (TextView) itemView.findViewById(R.id.txtIdSpMdp);
            txt_nom = (TextView) itemView.findViewById(R.id.txtNombreSpMdp);

            //Mostrar informaciòn en los Textview
            txt_id.setText(id_array_mdp.get(i).toString());
            txt_nom.setText(nom_array_mdp.get(i).toString());

            return itemView;
        }
    }

    public void ListarTipoComprobante() {

        //Limpiar los datos de los productos
        id_array_comp.clear();
        nom_array_comp.clear();

        //URL para acceder al archivo php
        String url = servidor + "listar_comprobante.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        //params.put("id",1);

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url,params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200) {//Ha procesado exitosamente
                    try {
                        JSONArray js = new JSONArray(new String(responseBody));

                        id_array_comp.add("");
                        nom_array_comp.add("Seleccione");

                        for (int i=0; i < js.length();i++) {
                            id_array_comp.add(String.valueOf(js.getJSONObject(i).getInt("id_comprobante")));
                            nom_array_comp.add(js.getJSONObject(i).getString("nom_comprobante"));
                        }
                        //Toast.makeText(getApplicationContext(),String.valueOf(js),Toast.LENGTH_LONG).show();
                        comp.setAdapter(null);
                        ListViewAdapterComprobante lva = new ListViewAdapterComprobante(requireContext());
                        comp.setAdapter(lva);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(requireContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ListViewAdapterComprobante extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        TextView txt_id2;
        TextView txt_nom2;

        public ListViewAdapterComprobante(Context context)
        {
            this.context = context;
        }

        @Override
        public int getCount() {
            return nom_array_comp.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_spinner_comp,null);

            txt_id2 = (TextView) itemView.findViewById(R.id.txtIdSpComp);
            txt_nom2 = (TextView) itemView.findViewById(R.id.txtNombreSpComp);

            //Mostrar informaciòn en los Textview
            txt_id2.setText(id_array_comp.get(i).toString());
            txt_nom2.setText(nom_array_comp.get(i).toString());

            return itemView;
        }
    }

    @Override
    public void onClick(View view) {
         if(view==finPedido) {
            if( id_mdp.isEmpty() || id_comp.isEmpty()) {
                Toast.makeText(requireContext(),"Debe elegir un elemento de la lista",Toast.LENGTH_LONG).show();
            }
            else if(contador<1) {
                Toast.makeText(requireContext(),"Debe agregar productos al carrito",Toast.LENGTH_LONG).show();
            }
            else {
                FinalizarPedido();
            }
        }
    }

    private void Eliminar(int id_producto) {
        //URL para acceder al archivo php
        String url = servidor + "eliminar_producto_carrito_pedido.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        params.put("id_pedido",id_pedido);
        params.put("id_producto",id_producto);

        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200) {//Ha procesado exitosamente
                    String respuesta = new String(responseBody);
                    if(respuesta.equals("SI")){
                        Toast.makeText(requireContext(),"Eliminado correctamente",Toast.LENGTH_LONG).show();
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_gallery);
                    }
                    else {
                        Toast.makeText(requireContext(),"Error al eliminar",Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(),"Error de ejecución",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void FinalizarPedido() {
        //URL para acceder al archivo php
        String url = servidor + "finalizar_carrito_pedido.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        params.put("id_pedido",id_pedido);
        params.put("id_cliente",id_cliente);
        params.put("id_mdp",id_mdp);
        params.put("id_comp",id_comp);
        //Toast.makeText(requireContext(),"P:"+id_pedido +"-C:"+id_cliente+"-M"+id_mdp+"-CP:"+id_comp,Toast.LENGTH_LONG).show();
        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(statusCode==200){//Ha procesado exitosamente
                    String respuesta = new String(responseBody);
                    if(respuesta.equals("SI")){
                        Pedido.setId(0);
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_home);
                        Toast.makeText(requireContext(),"Pedido registrado",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(requireContext(),"Error al registrar",Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(),"Error",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView==lista) {
            PopupMenu popupMenu = new PopupMenu(requireContext(),view);
            popupMenu.getMenuInflater().inflate(R.menu.opciones_carrito, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override public boolean onMenuItemClick(MenuItem menuItem) {
                    TextView tvId = (TextView) view.findViewById(R.id.txtIdP2);
                    int id_producto = Integer.parseInt(tvId.getText().toString());
                    switch (menuItem.getItemId()) {
                        case R.id.opc_eliminar:
                            Eliminar(id_producto);
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
         if (adapterView==mdp) {  //Spinner medio de pago
            TextView tvId = (TextView) view.findViewById(R.id.txtIdSpMdp);
            id_mdp = tvId.getText().toString();
        }
        else if (adapterView==comp) {  //Spinner comprobante
            TextView tvId = (TextView) view.findViewById(R.id.txtIdSpComp);
            id_comp = tvId.getText().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //void
    }
}