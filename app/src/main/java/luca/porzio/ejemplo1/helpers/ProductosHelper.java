package luca.porzio.ejemplo1.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import luca.porzio.ejemplo1.modelos.Producto;

public class ProductosHelper extends OrmLiteSqliteOpenHelper {
    private Dao<Producto, Integer> daoProductos;

    public ProductosHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Producto.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public Dao<Producto, Integer> getDaoProductos() {
        if (daoProductos == null) {
            try {
                daoProductos = getDao(Producto.class);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return daoProductos;
    }
}
