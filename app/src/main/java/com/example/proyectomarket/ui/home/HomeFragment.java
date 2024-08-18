package com.example.proyectomarket.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import com.example.proyectomarket.Usuario;
import com.example.proyectomarket.databinding.FragmentProductosBinding;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener{

    int id_pedido;
    int id_usuario;
    int id_cliente;
    //Pedido pedido = null;

    ListView listaP;

    ArrayList id_array = new ArrayList<>();
    ArrayList nom_array = new ArrayList();
    ArrayList tipo_array = new ArrayList();
    ArrayList prec_array = new ArrayList();
    ArrayList desc_array = new ArrayList();
    ArrayList stock_array = new ArrayList();

    final String servidor = "http://10.0.2.2/marketapp/";

    private FragmentProductosBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentProductosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listaP = (ListView) root.findViewById(R.id.lstProductos);
        listaP.setOnItemClickListener(this);

        //Obtener los id almacenados
        Bundle b = getActivity().getIntent().getExtras();
        Cliente cliente = (Cliente) b.getSerializable("cliente");
        id_cliente = cliente.getId();

        Usuario user = (Usuario) b.getSerializable("usuario");
        id_usuario = user.getId();

        id_pedido = Pedido.getId();

        //Toast.makeText(getContext (),"pedido: " + id_pedido + " id cliente " + id_cliente,Toast.LENGTH_LONG).show();

        ListarProducto();
        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void ListarProducto() {
        //Limpiar los datos de los productos
        id_array.clear();
        nom_array.clear();
        desc_array.clear();
        prec_array.clear();
        tipo_array.clear();
        stock_array.clear();

        //URL para acceder al archivo php
        String url = servidor + "listar_producto.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        //params.put("id",1);

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
                            stock_array.add(String.valueOf(js.getJSONObject(i).getInt("stock_producto")));
                        }
                        //Toast.makeText(requireContext(),String.valueOf(js),Toast.LENGTH_LONG).show();
                        listaP.setAdapter(null);
                        ListViewAdapterProducto lva = new ListViewAdapterProducto(requireContext());
                        listaP.setAdapter(lva);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getContext (),"Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Toast.makeText(requireContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ListViewAdapterProducto extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        TextView txt_id;
        TextView txt_nom;
        TextView txt_desc;
        TextView txt_prec;
        TextView txt_tipo;
        TextView txt_stock;

        public ListViewAdapterProducto(Context context)
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
            ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_producto,null);

            txt_id = (TextView) itemView.findViewById(R.id.txtIdP);
            txt_nom = (TextView) itemView.findViewById(R.id.txtNombreP);
            txt_desc = (TextView) itemView.findViewById(R.id.txtDescripcionP);
            txt_prec = (TextView) itemView.findViewById(R.id.txtPrecioP);
            txt_tipo = (TextView) itemView.findViewById(R.id.txtTipoP);
            txt_stock = (TextView) itemView.findViewById(R.id.txtStockP);

            //Mostrar informaciòn en los Textview
            txt_id.setText(id_array.get(i).toString());
            txt_nom.setText(nom_array.get(i).toString());
            txt_desc.setText("Descripción: "+desc_array.get(i).toString());
            txt_prec.setText("Precio: S/." + prec_array.get(i).toString());
            txt_tipo.setText("Tipo: "+ tipo_array.get(i).toString());
            txt_stock.setText("Stock: "+ stock_array.get(i).toString());

            return itemView;
        }
    }

    private void AgregarCarrito(int id){
        //URL para acceder al archivo php
        String url = servidor + "agregar_carrito_pedido.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
        params.put("id",id);
        params.put("id_pedido",id_pedido);
        params.put("id_cliente",id_cliente);
        //Toast.makeText(requireContext(),"Arg",Toast.LENGTH_LONG).show();
        //Para tener acceso al servidor web
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(url,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){    //Ha procesado exitosamente
                    String res = new String(responseBody);
                    if(!res.isEmpty()){ //Devuelve un ID de venta
                        Toast.makeText(requireContext(),"Agregado correctamente",Toast.LENGTH_LONG).show();
                        id_pedido= Integer.parseInt(res);
                        Pedido.setId(id_pedido);

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_home);
                        /*SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyObject", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pedido", new Gson().toJson(pedido));
                        editor.apply();*/
                    }
                    else{
                        Toast.makeText(requireContext(),"Error al agregar ",Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(),"Error de conexión",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == listaP){
            PopupMenu popupMenu = new PopupMenu(requireContext(),view);
            popupMenu.getMenuInflater().inflate(R.menu.opciones, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                @Override public boolean onMenuItemClick(MenuItem menuItem) {
                    TextView tvId = (TextView) view.findViewById(R.id.txtIdP);
                    int id = Integer.parseInt(tvId.getText().toString());

                    TextView tvStock = (TextView) view.findViewById(R.id.txtStockP);
                    String stock_t = tvStock.getText().toString();

                    String[] parts = stock_t.split(": ");
                    String part1 = parts[0]; // Stock
                    String part2 = parts[1]; // número
                    int stock = Integer.parseInt(part2);

                    switch (menuItem.getItemId()){
                        case R.id.opc_carrito_pedido:
                            if(stock>=1){
                                AgregarCarrito(id);
                            }
                            else{
                                Toast.makeText(requireContext(),"El producto no tiene stock",Toast.LENGTH_LONG).show();
                            }

                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }
}
