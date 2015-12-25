package com.go.fish.user;

import com.go.fish.data.PersonData;

public class User {

	public PersonData userInfo = null;
	static User sUser = null;
	private User(){}
	
	public static User self(){
		if(sUser == null){
			sUser = new User();
			sUser.userInfo = new PersonData();
		}
		return sUser;
	}
}
