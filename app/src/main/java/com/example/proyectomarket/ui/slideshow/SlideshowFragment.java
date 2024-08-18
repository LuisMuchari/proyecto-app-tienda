package com.example.proyectomarket.ui.slideshow;

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
import com.example.proyectomarket.PedidoDetalle;
import com.example.proyectomarket.R;
import com.example.proyectomarket.databinding.FragmentSlideshowBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SlideshowFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    ListView lista;
    TextView numPR,montoTP;

    int id_cliente,contador=0;
    double totalC=0;

    ArrayList idP_array = new ArrayList();
    ArrayList nomC_array = new ArrayList();
    ArrayList mdp_array = new ArrayList();
    ArrayList comp_array = new ArrayList();
    ArrayList fec_array = new ArrayList();
    ArrayList estado_array = new ArrayList();
    ArrayList monto_array = new ArrayList();

    final String servidor = "http://10.0.2.2/marketapp/";

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lista = (ListView) root.findViewById(R.id.lstPedido);
        lista.setOnItemClickListener(this);

        numPR = (TextView) root.findViewById(R.id.txtNumPR);
        montoTP = (TextView) root.findViewById(R.id.txtTotalP);

        Bundle b = getActivity().getIntent().getExtras();
        Cliente cliente = (Cliente) b.getSerializable("cliente");
        id_cliente = cliente.getId();
        //Toast.makeText(requireContext(),""+ id_cliente,Toast.LENGTH_LONG).show();

        ConsultarPedidos(id_cliente);
        //final TextView textView = binding.textSlideshow;
        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void ConsultarPedidos(int id_cliente) {
        //Limpiar los datos de los productos
        idP_array.clear();
        nomC_array.clear();
        mdp_array.clear();
        comp_array.clear();
        fec_array.clear();
        estado_array.clear();
        monto_array.clear();

        //URL para acceder al archivo php
        String url = servidor + "consultar_pedido.php";

        //Para tener parámetros
        RequestParams params = new RequestParams();
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
                        for (int i=0; i < js.length();i++) {
                            contador++;
                            idP_array.add(String.valueOf(js.getJSONObject(i).getInt("id_pedido")));
                            nomC_array.add(js.getJSONObject(i).getString("nombre_cliente"));
                            mdp_array.add(js.getJSONObject(i).getString("nombre_mdpago"));
                            comp_array.add(js.getJSONObject(i).getString("nombre_comprobante"));
                            fec_array.add(js.getJSONObject(i).getString("fec_pedido"));
                            estado_array.add(js.getJSONObject(i).getString("estado_pedido"));
                            monto_array.add(js.getJSONObject(i).getString("monto_total"));
                            totalC+=js.getJSONObject(i).getDouble("monto_total");
                        }
                        MostrarInforme();
                        //Toast.makeText(requireContext(),""+ contador/*String.valueOf(js)*/,Toast.LENGTH_LONG).show();
                        lista.setAdapter(null);
                        ListViewAdapterPedido lva = new ListViewAdapterPedido(requireContext());
                        lista.setAdapter(lva);
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
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Toast.makeText(requireContext(),"Error de ejecucion",Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ListViewAdapterPedido extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        TextView txt_idP;
        TextView txt_nomC;
        TextView txt_mpd;
        TextView txt_comp;
        TextView txt_fec;
        TextView txt_est;
        TextView txt_mt;

        public ListViewAdapterPedido(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return nomC_array.size();
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
            ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_pedidos,null);

            txt_idP = (TextView) itemView.findViewById(R.id.txtIdPd);
            txt_nomC = (TextView) itemView.findViewById(R.id.txtClientePd);
            txt_mpd = (TextView) itemView.findViewById(R.id.txtMdpPd);
            txt_comp = (TextView) itemView.findViewById(R.id.txtCompPd);
            txt_fec = (TextView) itemView.findViewById(R.id.txtFechaPd);
            txt_est = (TextView) itemView.findViewById(R.id.txtEstadoPd);
            txt_mt = (TextView) itemView.findViewById(R.id.txtMontoPd);

            //Mostrar informaciòn en los Textview
            txt_idP.setText("ID Pedido: "+ idP_array.get(i).toString());
            txt_nomC.setText("Cliente: "+nomC_array.get(i).toString());
            txt_mpd.setText("Medio de pago: "+mdp_array.get(i).toString());
            txt_comp.setText("Comprobante: "+comp_array.get(i).toString());
            txt_fec.setText("Fecha: "+fec_array.get(i).toString());
            txt_est.setText("Estado: "+"Registrado"/*estado_array.get(i).toString()*/);
            txt_mt.setText("Monto total: S/."+monto_array.get(i).toString());
            return itemView;
        }
    }

    private void MostrarInforme() {
        numPR.setText("Pedidos realizados: "+ contador);
        montoTP.setText("Total compras: S/."+ totalC);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView==lista) {
            PopupMenu popupMenu = new PopupMenu(requireContext(),view);
            popupMenu.getMenuInflater().inflate(R.menu.opciones_pedido, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override public boolean onMenuItemClick(MenuItem menuItem) {
                    TextView tvId = (TextView) view.findViewById(R.id.txtIdPd);
                    String id = tvId.getText().toString();

                    String[] parts = id.split(": ");
                    String part1 = parts[0]; // Texto
                    String part2 = parts[1]; // número

                    PedidoDetalle.setId(Integer.parseInt(part2));
                    switch (menuItem.getItemId()) {
                        case R.id.opc_consultar:
                            ConsultarDetalle();
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }

    private void ConsultarDetalle() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_pedidodetalle);
    }
}