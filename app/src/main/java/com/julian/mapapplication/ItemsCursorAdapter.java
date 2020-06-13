package com.julian.mapapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

public class ItemsCursorAdapter extends CursorAdapter {
    private Listener listener;
    public ItemsCursorAdapter(Context context, Cursor c, Listener listener) {
        super(context, c, 0);
        this.listener = listener;
    }

    public interface Listener{
        void deleteItem(String itemId);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item, viewGroup, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Referencias UI.
        TextView textView = view.findViewById(R.id.txt_info);

        String name = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.NAME));
        String country = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.COUNTRY));
        String height = cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.HEIGHT));

        textView.setText(name+" ("+country+")"+"\n*"+coordilleras+"*"+height);

        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteItem(cursor.getString(cursor.getColumnIndex(ItemsContract.ItemsEntry.ID)));
            }
        });

    }

}
