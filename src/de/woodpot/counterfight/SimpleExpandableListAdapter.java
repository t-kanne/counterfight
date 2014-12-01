package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

class SimpleExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;
	public ArrayList<DrawerItem[]> childItems;
	public DrawerItem[] childItem;
	ArrayList<DrawerItem> groupItems;
	DrawerItem groupData;
	DrawerItem childData;
	ArrayList<String> individualGroups;
	public LayoutInflater inflater;
	public Activity activity;
	int layoutCase;
	ViewHolderItem viewHolder;

	public SimpleExpandableListAdapter(Context context, ArrayList<DrawerItem> groupItems, ArrayList<DrawerItem[]> childItems) {
		super();
		this.context = context;
		this.groupItems = groupItems;
		this.childItems = childItems;
	}
	

	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {	
		View v = convertView;
				
		v = convertView;
		
		if (convertView == null) {	
			// inflate the layout
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(R.layout.drawer_list_item, parent, false);
	        
	        // set up the viewHolder
			viewHolder = new ViewHolderItem();
			viewHolder.childText = (TextView) v.findViewById(R.id.textview_navigationdrawer_row);
			viewHolder.childIcon = (ImageView) v.findViewById(R.id.imageview_navigationdrawer_row);
			
			// store the holder with the view
			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();
		}

		if (childPosition < childItem.length) {
			childData = childItem[childPosition];
			
			if (childData != null) {
				// use viewHolder for TextView
				viewHolder.childText.setText(childData.getTitle());
				viewHolder.childText.setTag(childData.getTitle());
				viewHolder.childIcon.setImageResource(childData.getIcon());
			}
		}
		v.setClickable(false); 													// false setzen, damit onChildClickListener() funktioniert
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 0;
		try {
			count = childItems.get(groupPosition).length;
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			return count;
		}
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		return groupItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);

	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}
	


	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View convertView, ViewGroup parent) {
		View v = convertView;
		
		groupData = groupItems.get(groupPosition);
		layoutCase = groupData.getLayoutType();				// Nummer des bevorzugten Layouts auslesen
		
		if (convertView == null) {
	        viewHolder = new ViewHolderItem();
	        
			switch (layoutCase) {				
				case 1: 
					inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        v = inflater.inflate(R.layout.drawer_section_title, parent, false);			        
			        viewHolder.groupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_sectiontitle);
					break;
					
				case 2:
					inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        v = inflater.inflate(R.layout.drawer_list_item, parent, false);
			        viewHolder.groupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_row);
					viewHolder.groupIcon = (ImageView) v.findViewById(R.id.imageview_navigationdrawer_row);
			        break;
					
				case 3: 
					inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        v = inflater.inflate(R.layout.drawer_list_item_textview_only, parent, false);
			        viewHolder.groupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_textviewonly_row);
			        break;
			}
			v.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolderItem) v.getTag();
		}
		
		if (groupData != null) {
			switch (layoutCase) {	
				case 1: 				
					viewHolder.groupText.setText(groupData.getTitle());				
					break;
						
				case 2:
					viewHolder.groupText.setText(groupData.getTitle());
					viewHolder.groupIcon.setImageResource(groupData.getIcon());
					break;
						
				case 3:
					if (isExpanded) {
						viewHolder.groupText.setText(context.getString((int) groupData.getExtras()));
					} else {
						viewHolder.groupText.setText(groupData.getTitle());
					}					
					break;
			}
		}
		childItem = childItems.get(groupPosition);
		v.setClickable(false);							// false setzen, damit onGroupClickListener() funktioniert
		return v;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// es muss true zurückgegeben werden, damit onChildClickListener() funktionieren kann
		return true;
	}
	
	static class ViewHolderItem {
		TextView childText;
		TextView groupText;	
		ImageView childIcon;
		ImageView groupIcon;
	}
	

		
	

}


