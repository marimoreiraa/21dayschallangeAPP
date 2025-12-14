package com.ifmg.a21dayschallangeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class IconSpinnerAdapter extends ArrayAdapter<IconItem> {

    public IconSpinnerAdapter(Context context, List<IconItem> iconList) {
        super(context, 0, iconList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent, true);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent, false);
    }

    private View createView(int position, View convertView, ViewGroup parent, boolean isSelectedView) {
        final IconItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_item_icon, parent, false
            );
        }

        ImageView iconView = convertView.findViewById(R.id.iconImageView);
        TextView nameView = convertView.findViewById(R.id.iconNameTextView);

        if (item != null) {
            iconView.setImageResource(item.getIconResourceId());
            nameView.setVisibility(View.GONE);
        }
        return convertView;
    }
}