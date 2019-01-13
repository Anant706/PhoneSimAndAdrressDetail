package com.makeinindia.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MAP extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private final Activity context;
    private final String[] names;

    public MAP(Activity context, String[] names) {
        super();
        this.context = context;
        this.names = names;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.g, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.textView1);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView1);
            convertView.setTag(viewHolder);

        }

        ViewHolder holder = (ViewHolder) convertView.getTag();


        String s = names[position];
        if (holder != null && s != null)
            holder.text.setText(s);

        switch (position) {
            case 0:
                holder.image.setImageResource(R.drawable.airtel);
                break;
            case 1:
                holder.image.setImageResource(R.drawable.vodafone);
                break;
            case 2:
                holder.image.setImageResource(R.drawable.idea);
                break;
            case 3:
                holder.image.setImageResource(R.drawable.telenor);
                break;
            case 4:
                holder.image.setImageResource(R.drawable.aircel);
                break;
            case 5:
                holder.image.setImageResource(R.drawable.reliance);
                break;
            case 6:
                holder.image.setImageResource(R.drawable.bsnl);
                break;
            case 7:
                holder.image.setImageResource(R.drawable.docomo);
                break;
            case 8:
                holder.image.setImageResource(R.drawable.videocon);
                break;
            case 9:
                holder.image.setImageResource(R.drawable.mts);
                break;
            case 10:
                holder.image.setImageResource(R.drawable.mtnl);
                break;
            case 11:
                holder.image.setImageResource(R.drawable.virgin);
                break;
            case 12:
                holder.image.setImageResource(R.drawable.loop);
                break;

        }
        return convertView;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }
} 