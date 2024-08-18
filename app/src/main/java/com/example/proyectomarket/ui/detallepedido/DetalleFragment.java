package com.example.proyectomarket.ui.detallepedido;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import com.example.proyectomarket.PedidoDetalle;
import com.example.proyectomarket.R;
import com.example.proyectomarket.databinding.FragmentDetallepedidoBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DetalleFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    ListView lista;
    int id_pedido,id_cliente;
    Button retornar;

    ArrayList id_array = new ArrayList();
    ArrayList nom_array = new ArrayList();
    ArrayList desc_array = new ArrayList();
    ArrayList tipo_array = new ArrayList();
    ArrayList prec_array = new ArrayList();
    ArrayList cant_array = new ArrayList();
    ArrayList total_array = new ArrayList();

    final String servidor = "http://10.0.2.2/marketapp/";

    private FragmentDetallepedidoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DetalleViewModel galleryViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(DetalleViewModel.class);
        binding = FragmentDetallepedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lista = (ListView) root.findViewById(R.id.lstPedidoDetalle);
        lista.setOnItemClickListener(this);

        //Botones
        retornar = (Button) root.findViewById(R.id.btnRetornar);
        retornar.setOnClickListener(this);


        id_pedido = PedidoDetalle.getId();

        Bundle b = getActivity().getIntent().getExtras();
        Cliente cliente = (Cliente) b.getSerializable("cliente");
        id_cliente = cliente.getId();

        ConsultarPedidoDetalle(id_pedido,id_cliente);

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void ConsultarPedidoDetalle(int id_pedido, int id_cliente) {
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
                        ListViewAdapterPedidoDetalle lva = new ListViewAdapterPedidoDetalle(requireContext());
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public class ListViewAdapterPedidoDetalle extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        TextView txt_id;
        TextView txt_nom;
        TextView txt_desc;
        TextView txt_prec;
        TextView txt_tipo;
        TextView txt_cant;
        TextView txt_total;
        public ListViewAdapterPedidoDetalle(Context context)
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

    @Override
    public void onClick(View view) {
         if(view==retornar) {
             NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
             navController.navigate(R.id.nav_slideshow);
        }
    }

}