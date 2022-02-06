package net.ivanvega.basededatoslocalconrooma.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.ivanvega.basededatoslocalconrooma.data.AppDatabase;
import net.ivanvega.basededatoslocalconrooma.data.User;
import net.ivanvega.basededatoslocalconrooma.data.UserDao;

import java.util.List;

public class MiContentProvider extends ContentProvider {
    /*Estructura de mi uri:
            uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user  -> insert y query
            uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user/#  -> update, query y delete
            uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user/*  -> query, update y delete}
                            net.ivanvega.basededatoslocalconrooma.provider
         */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI("net.ivanvega.basededatoslocalconrooma.provider", "user", 1);
        sUriMatcher.addURI("net.ivanvega.basededatoslocalconrooma.provider", "user/#", 2);
        sUriMatcher.addURI("net.ivanvega.basededatoslocalconrooma.provider", "user/*", 3);

    }
    //MAPEADO COMO LO QUERIAMOS

    Cursor listUserToCursorUser(List<User> usuarios){
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "uid", "first_name", "last_name"
        });
        for (User usuario: usuarios) {
            matrixCursor.newRow().add("uid", usuario.uid).
                    add("first_name", usuario.firstName).
                    add("last_name", usuario.lastName);

        }
        return matrixCursor;
    }

    Cursor userToCursorUser(User usuario){
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "uid", "first_name", "last_name"
        });
        matrixCursor.newRow().add("uid", usuario.uid).
                add("first_name", usuario.firstName).
                add("last_name", usuario.lastName);

        return matrixCursor;
    }

    @Override
    public boolean onCreate() {

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String s1) {
        AppDatabase db =
                AppDatabase.getDatabaseInstance(getContext());
        Cursor cursor = null;
        UserDao dao = db.userDao();
        Log.d("nombre", uri.toString());
        switch (sUriMatcher.match(uri)){
            case 1:
                cursor = listUserToCursorUser( dao.getAll());
                break;
            case 2:
                String id = uri.getLastPathSegment();
                cursor = userToCursorUser(dao.selectUser(Integer.parseInt(id)));
                break;
            case 3:
                String s = uri.getLastPathSegment();
                cursor = listUserToCursorUser(dao.listNombres(s));
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String typeMIME = "";
        switch (sUriMatcher.match(uri)){
            case 1:
                typeMIME = "vnd.android.cursor.dir/vnd.net.ivanvega.basededatoslocalconrooma.provider.user";
                break;
            case 2:
                typeMIME = "vnd.android.cursor.item/vnd.net.ivanvega.basededatoslocalconrooma.provider.user";
                break;
            case 3:
                typeMIME = "vnd.android.cursor.dir/vnd.net.ivanvega.basededatoslocalconrooma.provider.user";
                break;

        }
        return typeMIME;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        AppDatabase db =
                AppDatabase.getDatabaseInstance(getContext());
        Cursor cursor = null;
        UserDao dao = db.userDao();
        User usuario = null;
        switch (sUriMatcher.match(uri)){
            case 1:
                usuario = new User();
                usuario.firstName = values.getAsString(UsuarioContrato.COLUMN_FIRSTNAME);
                usuario.lastName = values.getAsString(UsuarioContrato.COLUMN_LASTNAME);

                long newid = dao.insert(usuario);

                return Uri.withAppendedPath(uri, String.valueOf(newid));

                //break;
        }
        return Uri.withAppendedPath(uri,String.valueOf(-1));
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int id = Integer.parseInt(uri.getLastPathSegment());
        AppDatabase db =
                AppDatabase.getDatabaseInstance(getContext());
        UserDao dao = db.userDao();
        User usuario = dao.selectUser(id);
        return dao.deleteUsuario(usuario);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int id = Integer.parseInt(uri.getLastPathSegment());
        AppDatabase db =
                AppDatabase.getDatabaseInstance(getContext());
        Cursor cursor = null;
        UserDao dao = db.userDao();
        List<User> usuarioUpdate = dao.loadAllByIds(new int[]{id});

        usuarioUpdate.get(0).firstName= values.getAsString(UsuarioContrato.COLUMN_FIRSTNAME);

        usuarioUpdate.get(0).lastName = values.getAsString(UsuarioContrato.COLUMN_LASTNAME);

        return dao.updateUser(usuarioUpdate.get(0));
    }
}
