package com.camel.redpenguin.ui.fragment;


import com.camel.redpenguin.R;
import com.camel.redpenguin.ui.FragmentBase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @ClassName : MainMenuFragment
 * @Description: 菜单
 * @author kcj
 * @date
 */
public class MainMenuFragment extends FragmentBase{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_menu, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}
	
}
