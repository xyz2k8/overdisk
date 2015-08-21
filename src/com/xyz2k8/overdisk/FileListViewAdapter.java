package com.xyz2k8.overdisk;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileListViewAdapter extends ArrayAdapter<ListItem>{
	
	int resource;
	
	public FileListViewAdapter(Context _context, int _resource, ArrayList<ListItem> _items) {
	    super(_context, _resource, _items);
	    resource = _resource;
	}
	  
	
	  @Override
	  public View getView(final int position, View convertView, ViewGroup parent) {
          LinearLayout layout;
  
          ListItem item = getItem(position);
  
          if (convertView == null) 
          {
        	  layout = new LinearLayout(getContext());
			  String inflater = Context.LAYOUT_INFLATER_SERVICE;
			  LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			  vi.inflate(resource, layout, true);
		  }
		  else 
		  {
			  layout = (LinearLayout) convertView;
		  }
          
          TextView name = (TextView)layout.findViewById(R.id.item_name);
          TextView type = (TextView)layout.findViewById(R.id.item_type);
          TextView size = (TextView)layout.findViewById(R.id.item_size);
          
          name.setText(item.itemName);          
          type.setText(item.itemType);
          size.setText(item.itemSize);
          
          if(item.itemName.equals(".."))
          {
        	  type.setText("");
          }
          
          if(item.itemType.equals("D"))
          {
        	  size.setVisibility(View.GONE);
          }
          else
          {
        	  size.setVisibility(View.VISIBLE);
          }
		  
	      return layout;
	  }

}
