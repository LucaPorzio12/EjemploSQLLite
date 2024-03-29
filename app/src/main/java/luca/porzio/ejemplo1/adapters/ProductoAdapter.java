package luca.porzio.ejemplo1.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import luca.porzio.ejemplo1.R;
import luca.porzio.ejemplo1.configuracion.Configuracion;
import luca.porzio.ejemplo1.helpers.ProductosHelper;
import luca.porzio.ejemplo1.modelos.Producto;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoVH> {
    private Context context;
    private List<Producto> objects;
    private int resource;

    private ProductosHelper helper;
    private Dao<Producto, Integer> daoProductos;


    public ProductoAdapter(Context context, List<Producto> objects, int resource) {
        this.context = context;
        this.objects = objects;
        this.resource = resource;
        helper = new ProductosHelper(context, Configuracion.BD_NAME, null, Configuracion.BD_VERSION);

        if (helper != null) {

            daoProductos = helper.getDaoProductos();

        }
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(context).inflate(resource, null);

        productView.setLayoutParams(
                new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        return new ProductoVH(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        Producto producto = objects.get(position);
        holder.lbNombre.setText(producto.getNombre());
        holder.lbTotal.setText(String.valueOf(producto.getTotal()));

        holder.imEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarEditar(holder.getAdapterPosition()).show();
            }
        });
        holder.imDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarBorrar(holder.getAdapterPosition()).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private AlertDialog confirmarEditar(int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("MODIFICAR PRODUCTO");
        builder.setCancelable(false);

        View productView = LayoutInflater.from(context).inflate(R.layout.product_view_model, null);
        EditText txtNombre = productView.findViewById(R.id.txtNombreProductViewModel);
        EditText txtPrecio = productView.findViewById(R.id.txtPrecioProductViewModel);
        EditText txtCantidad = productView.findViewById(R.id.txtCantidadProductViewModel);
        builder.setView(productView);

        Producto producto = objects.get(posicion);
        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getPrecio()));

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!txtNombre.getText().toString().isEmpty() &&
                        !txtPrecio.getText().toString().isEmpty() &&
                        !txtCantidad.getText().toString().isEmpty()) {
                    producto.setNombre(txtNombre.getText().toString());
                    producto.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    producto.setPrecio(Float.parseFloat(txtPrecio.getText().toString()));
                    notifyItemChanged(posicion);
                    try {
                        daoProductos.update(producto);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        return builder.create();
    }

    private AlertDialog confirmarBorrar(int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ESTÁS SEGURO???");
        builder.setCancelable(false);
        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    daoProductos.deleteById(objects.get(posicion).getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                objects.remove(posicion);
                notifyItemRemoved(posicion);
            }
        });

        ;
        return builder.create();
    }

    public class ProductoVH extends RecyclerView.ViewHolder {
        TextView lbNombre, lbTotal;
        ImageButton imEdit, imDelete;

        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            lbNombre = itemView.findViewById(R.id.lbNombrePorductViewHolder);
            lbTotal = itemView.findViewById(R.id.lbTotalPorductViewHolder);

            imEdit = itemView.findViewById(R.id.imEditProductViewHolder);
            imDelete = itemView.findViewById(R.id.imDeleteProductViewHolder);
        }
    }
}
