package com.johnwang.trident.trident.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnwang.trident.trident.Property;
import com.johnwang.trident.trident.R;

import java.util.List;

/**
 * Property listing adapter.
 */
public class PropertyListingAdapter extends ArrayAdapter<Property> {

    private final Context context;
    private final List<Property> properties;

    public PropertyListingAdapter(Context context, int resource, List<Property> properties) {
        super(context, resource, properties);
        this.context = context;
        this.properties = properties;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.property_listing_item, parent, false);
        }
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.property_listing_thumbnail);
        thumbnail.setImageBitmap(properties.get(position).getThumbnail());
        final int p = position;
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(properties.get(p).getDetailUrl()));
                context.startActivity(intent);
            }
        });

        TextView streetName = (TextView) convertView.findViewById(R.id.property_listing_street_name);
        streetName.setText(properties.get(position).getStreetName());
        TextView price = (TextView) convertView.findViewById(R.id.property_listing_price);
        price.setText(String.format("£%s", properties.get(position).getPrice()));
        TextView agentPhone = (TextView) convertView.findViewById(R.id.property_listing_agent_phone);
        agentPhone.setText(properties.get(position).getAgentPhone());
        return convertView;
    }
}
