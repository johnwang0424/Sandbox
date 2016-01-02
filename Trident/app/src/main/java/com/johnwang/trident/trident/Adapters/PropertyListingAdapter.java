package com.johnwang.trident.trident.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.johnwang.trident.trident.Property;

import java.util.List;

/**
 * Created by johnwang on 1/1/16.
 */
public class PropertyListingAdapter extends ArrayAdapter<Property> {

    public PropertyListingAdapter(Context context, int resource, List<Property> properties) {
        super(context, resource, properties);
    }
}
