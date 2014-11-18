package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AllGroupsActivityAdapter extends BaseAdapter {
	private Map<String, String> users = new HashMap<String,String>(); 
	private String[] usersArray;
	private final LayoutInflater inflater;
	
	public AllGroupsActivityAdapter(Context context, Map<String, String> users){
		this.users = users;
		usersArray = users.keySet().toArray(new String[users.size()]);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public String getItem(int index) {
		return users.get(usersArray[index]);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	static class ViewHolder {
		TextView groupName;
		TextView firstPlace;
		TextView ownPlace;
	}
	
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		Log.d("ÂllGroupsActivityAdapter JSON: ", "wert: " + usersArray[index]);
		
		ViewHolder holder;
		
		if (convertView == null){
			convertView = inflater.inflate(R.layout.all_groups_list_item, null);
			
			holder = new ViewHolder();
			holder.groupName = (TextView) convertView.findViewById(R.id.user_row_groupName);
			holder.firstPlace = (TextView) convertView.findViewById(R.id.user_row_first_place);
			holder.ownPlace = (TextView) convertView.findViewById(R.id.user_row_own_place);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.groupName.setText(usersArray[index]);
		holder.firstPlace.setText("1.:" + getItem(index));
		holder.ownPlace.setText("Du: " + getItem(index));
		
		return convertView;
	}
	
	
	

}