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
	public View viewHolder;

	public SimpleExpandableListAdapter(Context context, ArrayList<DrawerItem> groupItems, ArrayList<DrawerItem[]> childItems) {
		super();
		this.context = context;
		this.groupItems = groupItems;
		this.childItems = childItems;
			
		Log.d("SimpleExpListAdapter", "Konstruktor ausgeführt");
		Log.d("SimpleExpListAdapter", "Context: " + context.toString());
		Log.d("SimpleExpListAdapter", "groupItems: " + groupItems.toString());
		Log.d("SimpleExpListAdapter", "childItem: " + childItems.toString());
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
		
		childItem = childItems.get(groupPosition);
		Log.d("SimpleExpListAdapter", "childItems.get(" + groupPosition + ") groupPosition");
		
		if (childPosition < childItem.length) {
			childData = childItem[childPosition];
		}

		Log.d("SimpleExpListAdapter", "gchildItem[" + childPosition + "] childPosition");
		
		View v = convertView;
		
		if (convertView == null) {
			Log.d("SimpleExpListAdapter", "getChildView childPos: " + childPosition);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(R.layout.drawer_list_item, parent, false);
		}
		TextView childText = (TextView) v.findViewById(R.id.textview_navigationdrawer_row);
		childText.setText(childData.getTitle());
		ImageView childImage = (ImageView) v.findViewById(R.id.imageview_navigationdrawer_row);
		childImage.setImageResource(childData.getIcon());
		
		
		viewHolder = v;
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 0;
		try {
			count = childItems.get(groupPosition).length;
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
		TextView mGroupText;
		ImageView mGroupIcon;
		
		Log.d("SimpleExpListAdapter", "getGroupView groupPos: " + groupPosition);
		
		groupData = groupItems.get(groupPosition);
		int layoutCase = (int) groupData.getLayoutType();				// Nummer des bevorzugten Layouts auslesen
		
		if (convertView == null) {
			switch (layoutCase) {				
				case 1: 
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
				Log.d("SimpleExpListAdapter", "case1 KEY_TITLE: " + groupData.getTitle());
				mGroupText.setText(groupData.getTitle());
				break;
					
			case 2:
				mGroupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_row);
				mGroupText.setText(groupData.getTitle());
				mGroupIcon = (ImageView) v.findViewById(R.id.imageview_navigationdrawer_row);
				mGroupIcon.setImageResource(groupData.getIcon());
				break;
					
			case 3:
				mGroupText = (TextView) v.findViewById(R.id.textview_navigationdrawer_textviewonly_row);
				Log.d("SimpleExpListAdapter", "case3 KEY_TITLE: " + groupData.getTitle());
				mGroupText.setText(groupData.getTitle());	
				break;
		};
		
		Log.d("SimpleExpListAdapter", "View: " + v);
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


