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
	public ArrayList<Object> childItem;
	ArrayList<HashMap<String, Object>> groupItem;
	HashMap<String, Object> groupData;
	public ArrayList<String> childTemp;
	public LayoutInflater inflater;
	public Activity activity;
	public View viewHolder;

	public SimpleExpandableListAdapter(Context context, ArrayList<HashMap<String, Object>> groupItems, ArrayList<Object> childItem) {
		super();
		this.context = context;
		groupItem = groupItems;
		this.childItem = childItem;
			
		Log.d("SimpleExpListAdapter", "Konstruktor ausgeführt");
		Log.d("SimpleExpListAdapter", "Context: " + context.toString());
		Log.d("SimpleExpListAdapter", "groupItem: " + groupItems.get(0));
		Log.d("SimpleExpListAdapter", "childItem: " + childItem.get(0));
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
		Log.d("SimpleExpAdapter: ", "getChildView() ausgeführt");
		childTemp = (ArrayList<String>) childItem.get(groupPosition);
		View v = convertView;
		
		if (convertView == null) {
			Log.d("SimpleExpListAdapter", "getChildView childPos: " + childPosition);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(R.layout.drawer_list_item, parent, false);
		}
		TextView childText = (TextView) v.findViewById(R.id.textview_navigationdrawer_onegroup);
		childText.setText(childTemp.get(childPosition));
		
		viewHolder = v;
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 0;
		try {
			count = ((ArrayList<Object>) childItem.get(groupPosition)).size();
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			Log.d("SimpleExpListAdapter", "keine Child-Elemente");
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
		return groupItem.size();
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
		View expV = convertView;
		TextView mGroupText;
		ImageView mGroupIcon;
		
		Log.d("SimpleExpListAdapter", "getGroupView groupPos: " + groupPosition);
		
		groupData = groupItem.get(groupPosition);
		int layoutCase = (int) groupData.get("KEY_LAYOUT");
		
		if (convertView == null) {
			switch (layoutCase) {				
				case 1: 
					Log.d("SimpleExpListAdapter", "LayoutCase: " + layoutCase);
					inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        v = inflater.inflate(R.layout.drawer_section_title, parent, false);
					break;
					
				case 2:
					Log.d("SimpleExpListAdapter", "LayoutCase: " + layoutCase);
					inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        v = inflater.inflate(R.layout.drawer_list_item, parent, false);
			        break;
					
				case 3: 
					Log.d("SimpleExpListAdapter", "LayoutCase: " + layoutCase);
					inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        v = inflater.inflate(R.layout.drawer_list_item_textview_only, parent, false);
			        break;
			};
		}
			
		switch (layoutCase) {	
			case 1: 
				mGroupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_sectiontitle);
				Log.d("SimpleExpListAdapter", "KEY_TITLE: " + groupData.get("KEY_TITLE").toString());
				mGroupText.setText(groupData.get("KEY_TITLE").toString());
				break;
					
			case 2:
				mGroupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_onegroup);
				mGroupText.setText(groupData.get("KEY_TITLE").toString());
				mGroupIcon = (ImageView) v.findViewById(R.id.imageview_navigationdrawer_onegroup);
				mGroupIcon.setImageResource((int) groupData.get("KEY_ICON"));
				break;
					
			case 3:
				mGroupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_grouptitle);
				mGroupText.setText(groupData.get("KEY_TITLE").toString());	
				break;
		};
	
		return v;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}


