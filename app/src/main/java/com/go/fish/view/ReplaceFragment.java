package com.go.fish.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.PersonData;
import com.go.fish.user.User;
import com.go.fish.util.BaseUtils;
import com.go.fish.util.Const;
import com.go.fish.util.UrlUtils;

public class ReplaceFragment extends Fragment{

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		int layoutId = getArguments().getInt(Const.PRI_LAYOUT_ID);
		View view = inflater.inflate(layoutId,container,false);
		if(layoutId == R.layout.ui_my_sec){
			PersonData userInfo = User.self().userInfo;
			ImageView iv = (ImageView)view.findViewById(R.id.reg_next_photo);
			if(!TextUtils.isEmpty(userInfo.photoUrl)){
				ViewHelper.load(iv, UrlUtils.self().getNetUrl(userInfo.photoUrl), true);
			}
			{
				TextView reg_next_account = (TextView)view.findViewById(R.id.reg_next_account);
				reg_next_account.setText(userInfo.id);
			}
			{
				TextView reg_next_phone_num = (TextView)view.findViewById(R.id.reg_next_phone_num);
				if(!TextUtils.isEmpty(userInfo.mobileNum)){
					reg_next_phone_num.setText(BaseUtils.formatPhoneNum(userInfo.mobileNum));
				}
			}
			{
				TextView reg_next_nick_input = (TextView)view.findViewById(R.id.reg_next_nick_input);
				reg_next_nick_input.setText(userInfo.userName);
			}
			{
				TextView reg_next_fishing_years_spinner = (TextView)view.findViewById(R.id.reg_next_fishing_years_spinner);
				if(!TextUtils.isEmpty(userInfo.fYears)){
					reg_next_fishing_years_spinner.setText(userInfo.fYears);
				}
			}
			{
				TextView reg_next_fishing_times_spinner = (TextView)view.findViewById(R.id.reg_next_fishing_times_spinner);
				if(!TextUtils.isEmpty(userInfo.fTimes)){
					reg_next_fishing_times_spinner.setText(userInfo.fTimes);
				}
			}
			{
				TextView reg_next_location_input = (TextView)view.findViewById(R.id.reg_next_location_input);
				if(!TextUtils.isEmpty(userInfo.address)){
					reg_next_location_input.setText(userInfo.address);
				}
			}
		}
        return view;
	}
}
