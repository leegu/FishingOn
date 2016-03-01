package com.go.fish.ui;

public interface UICode {

	public static interface RequestCode{ 
		int REQUEST_PICK_IMAGE = 1001;
		int REQUEST_PICK_IMAGE_MULTIPLE = 1002;
		int REQUEST_PICK_IMAGE_SYSTEM_MULTIPLE = 1003;
		int REQUEST_CROP_IMAGE = 1004;
		int REQUEST_PICK_CAPTURE = 1005;
		int REQUEST_BARCODE = 1;
		int REQUEST_PODCAST_PUBLISH = 2;
	}
	
	public static interface ResultCode{
		int RESULT_BARCODE_QR = 1001;
	}
}
