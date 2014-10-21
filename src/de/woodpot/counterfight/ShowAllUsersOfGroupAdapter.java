package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShowAllUsersOfGroupAdapter extends BaseAdapter {
	private Map<String, String> users = new HashMap<String,String>(); 
	private String[] usersArray;
	private final LayoutInflater inflater;
	
	public ShowAllUsersOfGroupAdapter(Context context, Map<String, String> users){
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
		TextView userName;
		TextView counterValue;
		//ImageView categoryRowIcon;
	}
	
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if (convertView == null){
			convertView = inflater.inflate(R.layout.category_row_layout, null);
			
			holder = new ViewHolder();
			holder.userName = (TextView) convertView.findViewById(R.id.user_row_username);
			holder.counterValue = (TextView) convertView.findViewById(R.id.user_row_countervalue);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.userName.setText(usersArray[index]);
		holder.counterValue.setText(getItem(index));
		
		return convertView;
	}
	
	
	

}