package com.camel.redpenguin.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.util.PhotoUtil;
import com.camel.redpenguin.util.PixelUtil;
import com.camel.redpenguin.view.EditViewDialog;

/**
 * @ClassName: DeviceInfoActivity
 * @Description: 设置设备信息
 * @author kcj
 * @date
 */
public class DeviceInfoActivity extends BaseActivity implements OnClickListener{
	
	RelativeLayout rlDeviceIcon;
	ImageView ivDeviceIcon;
	RelativeLayout rlDeviceName;
	TextView tvDeviceContent;
	RelativeLayout rlDeviceTdcode;
	TextView tvCancelDevice;
	
	EditViewDialog mEditViewDialog;
	
	CamelDevice mCamelDevice;
	// 头像选择
	AlertDialog albumDialog;
	public String filePath = "";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deviceinfo);
		/** header */
		initTopBarForLeft("基本信息");
		/** UI */
		rlDeviceIcon = (RelativeLayout)findViewById(R.id.rl_device_icon);
		ivDeviceIcon = (ImageView)findViewById(R.id.iv_device_icon);
		rlDeviceName = (RelativeLayout)findViewById(R.id.rl_device_name);
		tvDeviceContent = (TextView)findViewById(R.id.tv_device_name_content);
		rlDeviceTdcode = (RelativeLayout)findViewById(R.id.rl_device_tdcode);
		tvCancelDevice = (TextView)findViewById(R.id.tv_device_cancel_attention);
		rlDeviceIcon.setOnClickListener(this);
		rlDeviceName.setOnClickListener(this);
		rlDeviceTdcode.setOnClickListener(this);
		tvCancelDevice.setOnClickListener(this);
		/**  */
		mEditViewDialog = new EditViewDialog(this);
		mEditViewDialog.setTitle("修改昵称");
		mEditViewDialog.setButton1("确定",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String name = mEditViewDialog.getDeletableEditText();
				mCamelDevice.setDeviceNike(name);;
				DBHelper.getInstance(mContext).updateToDevicesInfoTable(mCamelDevice);
				refresh();
				mEditViewDialog.cancel();
			}
			
		});
		mEditViewDialog.setButton2("取消",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				mEditViewDialog.cancel();
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}
	
	public void refresh(){
		try {
			this.runOnUiThread(new Runnable() {
				public void run() {
					queryMyDevices();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void queryMyDevices(){	 
		mCamelDevice = CustomApplcation.getInstance().getCurrentCamelDevice();
		String Avatar = mCamelDevice.getDeviceAvatar(); // 头像
		if (!TextUtils.isEmpty(Avatar)) {
			File mFile=new File(Avatar);
	        //若该文件存在
	        if (mFile.exists()) {
	            Bitmap bitmap=BitmapFactory.decodeFile(mCamelDevice.getDeviceAvatar());
	            Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, PixelUtil.dp2px(80), PixelUtil.dp2px(80), true);
	            ivDeviceIcon.setImageBitmap(mBitmap);
	        }
		}else{
        	Bitmap btm1 = BitmapFactory.decodeResource(this.getResources(),
    				R.drawable.default_head);
    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(80), PixelUtil.dp2px(80), true);
    		ivDeviceIcon.setImageBitmap(mBitmap);
        }
		
		String Nike = mCamelDevice.getDeviceNike(); // 昵称
		tvDeviceContent.setText(!TextUtils.isEmpty(Nike)?Nike:"");
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_device_icon:
			if(mCamelDevice == null){
				ShowToast("无设备");
				return;
			}
			showAlbumDialog();
			break;
		case R.id.rl_device_name:
			if(mCamelDevice == null){
				ShowToast("无设备");
				return;
			}
			mEditViewDialog.show();
			break;
		case R.id.rl_device_tdcode:
			if(mCamelDevice == null){
				ShowToast("无设备");
				return;
			}
			Intent intent = new Intent(this , CodeCreateActivity.class); 
			intent.putExtra("URL", mCamelDevice.getDeviceIdentify());
			startActivity(intent);
			break;
		case R.id.tv_device_cancel_attention:
			if(mCamelDevice == null){
				ShowToast("无设备");
				return;
			}
			showLoadingDialog("取消关注");
			SocketInit.getInstance().cancelDevice(mCamelDevice.getDeviceIdentify(), new SaveListener(){
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						public void run() {
							cancelDevice();
						}
					});
					Intent intent = new Intent(); 
					intent.setAction("action.main.refresh.2a");  
					sendBroadcast(intent); 
				}

				@Override
				public void onFailure(int paramInt, String paramString) {
					// TODO Auto-generated method stub
					dismissLoadingDialog();
					ShowToast(paramString);
					Log.i("onLoginFailure", paramString.toString());
				}
				
			});
			break;
		}
	}
	
	public void cancelDevice(){
		//deviceSelector = false;
		DBHelper.getInstance(mContext).deleteDevicesInfoList(mCamelDevice.getDeviceIdentify()) ;
		List<CamelDevice> existList = DBHelper.getInstance(mContext).getDevicesInfoList();
		for(int i = 0 ; i < existList.size() ; i++){
			CamelDevice camel = DBHelper.getInstance(mContext).getAssignDevices(existList.get(i).getDeviceIdentify()).get(0);
			if(camel.getDeviceSelect().equals("true")){
				camel.setDeviceSelect("false");
			}
			DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel) ;
		}
		List<CamelDevice> listDevice = DBHelper.getInstance(mContext).getDevicesInfoList();
		if(listDevice == null || listDevice.size() == 0){
			finish();
			return;
		}
		CamelDevice updateCamelDevice = listDevice.get(0);
		updateCamelDevice.setDeviceSelect("true");
		DBHelper.getInstance(mContext).updateToDevicesInfoTable(updateCamelDevice) ;
		
		dismissLoadingDialog();
		finish();

	}
	
	public void showAlbumDialog(){
		albumDialog = new AlertDialog.Builder(mContext).create();
		albumDialog.setCanceledOnTouchOutside(true);
		View v = LayoutInflater.from(mContext).inflate(R.layout.include_dialog_usericon, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);
		TextView albumPic = (TextView)v.findViewById(R.id.album_pic);
		TextView cameraPic = (TextView)v.findViewById(R.id.camera_pic);
        albumPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				albumDialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,
						Config.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});
		cameraPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				albumDialog.dismiss();
				File dir = new File(Config.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
				filePath = file.getAbsolutePath();// 获取相片的保存路径
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,Config.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
	}
	
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Config.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.e("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						Config.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case Config.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
//			if (avatorPop != null) {
//				avatorPop.dismiss();
//			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			//uploadAvatar();
			break;
		case Config.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
//			if (avatorPop != null) {
//				avatorPop.dismiss();
//			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,
						Config.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		}
	}

	/**
	 * @Title: startImageAction
	 * @return void
	 * @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	
	String path;
	
	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				ivDeviceIcon.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				path = Config.MyAvatarDir + filename;
				Log.e("TAG", path);
				// 保存图片到存储库
				mCamelDevice.setDeviceAvatar(path);
				DBHelper.getInstance(mContext).updateToDevicesInfoTable(mCamelDevice);
				PhotoUtil.saveBitmap(Config.MyAvatarDir, filename,
						bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}
}
