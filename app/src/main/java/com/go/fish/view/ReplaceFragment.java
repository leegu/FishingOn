package com.go.fish.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.go.fish.util.Const;

public class ReplaceFragment extends Fragment{

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		int layoutId = getArguments().getInt(Const.LAYOUT_ID);
		View view = inflater.inflate(layoutId,container,false);
        return view;
	}
}
