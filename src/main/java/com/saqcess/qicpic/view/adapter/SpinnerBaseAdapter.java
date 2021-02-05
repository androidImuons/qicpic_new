package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.model.ImageModel;

import java.util.ArrayList;

public class SpinnerBaseAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private  Context context;
ArrayList<ImageModel> folderlist;
    public SpinnerBaseAdapter(Context applicationContext, ArrayList<ImageModel> al_images){
        context=applicationContext;
        folderlist=al_images;
       inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

   }
    @Override
    public int getCount() {
        return folderlist.size();
    }

    @Override
    public Object getItem(int i) {
        return folderlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder binding;
        if (view == null) {
            binding = new ViewHolder();
            view = inflater.inflate(
                    R.layout.spinner_view, null);
            binding.textView = (TextView) view.findViewById(R.id.txt_folder_name);
            view.setTag(binding);
        } else {
            binding = (ViewHolder) view.getTag();
        }
            binding.textView.setText(folderlist.get(i).getStr_folder());
        return view;
    }
    private class ViewHolder{
        TextView textView;
    }
}
