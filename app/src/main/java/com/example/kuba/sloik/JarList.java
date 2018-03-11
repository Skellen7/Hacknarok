package com.example.kuba.sloik;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kuba on 11.03.2018.
 */

public class JarList extends ArrayAdapter<JarClass> {
    private Activity context;
    private List<JarClass> jarList;

    public JarList(Activity context, List<JarClass> jarList){
        super(context, R.layout.activity_main, jarList);
        this.context = context;
        this.jarList = jarList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem= inflater.inflate(R.layout.activity_main, null, true);

        return listViewItem;
    }
}
