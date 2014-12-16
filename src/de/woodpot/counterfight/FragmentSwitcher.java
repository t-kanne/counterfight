package de.woodpot.counterfight;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public interface FragmentSwitcher {
	
	public void replaceFragment(Bundle fragmentData, Fragment fragment);
	
	public void startGroupDependingActivity(String noOfGroups);

}
