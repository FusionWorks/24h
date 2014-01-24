package com.twentyfour.menu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twentyfour.R;

public class MenuAdapter extends ArrayAdapter<String>{
	private String[] items;
	private int menuItemLayoutResource;
    public NavMenu parentClass;
    //private icon list 
    public MenuAdapter(Context context, String[] in_items, int menuItemLayoutResource, NavMenu parentClass){
    	super(context, 0);
        this.parentClass = parentClass;
    	this.menuItemLayoutResource = menuItemLayoutResource;
    	items = in_items;
    }
    
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public String getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// We need to get the best view (re-used if possible) and then
		// retrieve its corresponding ViewHolder, which optimizes lookup efficiency
		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final String entry = getItem(position);

        // Setting the title view is straightforward
        viewHolder.titleView.setText(entry);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentClass.selectedItem(position);
                Log.v("CL", "row clicked");
            }
        });
        return view;



	}
	
	private View getWorkingView(final View convertView) {
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;
		if(null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater)context.getSystemService
		      (Context.LAYOUT_INFLATER_SERVICE);
			
			workingView = inflater.inflate(menuItemLayoutResource, null);
		}else{
			workingView = convertView;
		}
		return workingView;
	}
	
	private ViewHolder getViewHolder(final View workingView) {
		// The viewHolder allows us to avoid re-looking up view references
		// Since views are recycled, these references will never change
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;
		
		if(null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();
			viewHolder.titleView = (TextView) workingView.findViewById(R.id.menu_item_title);
			workingView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) tag;
		}
		return viewHolder;
	}
	
	/**
	 * ViewHolder allows us to avoid re-looking up view references
	 * Since views are recycled, these references will never change
	 */
	private static class ViewHolder {
		public TextView titleView;
	}
}