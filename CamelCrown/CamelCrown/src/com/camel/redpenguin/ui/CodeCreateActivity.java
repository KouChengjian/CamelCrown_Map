package com.camel.redpenguin.ui;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import com.camel.redpenguin.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: CodeActivity
 * @Description: 生成二维码
 * @author kcj
 * @date
 */
public class CodeCreateActivity extends BaseActivity {
	private ImageView imageView;
	private BitMatrix matrix;
	private TextView tvId;
	private String url ;

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_code);
		Intent intent = getIntent();
		url =  intent.getStringExtra("URL").toString();
		initTopBarForLeft("二维码");
		if(TextUtils.isEmpty(url)){
			ShowToast("无设备");
			return;
		}
		imageView = (ImageView) findViewById(R.id.iv_code);
		tvId = (TextView) findViewById(R.id.tv_info_id);
		tvId.setText("手表内SIM卡号 : " + url);
		Bitmap bitmap = null;
		try {
			if (url != null && !"".equals(url)) {
				bitmap = CreateCode(url);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}

	}

	@SuppressWarnings("unused")
	private Bitmap CreateCode(String content) throws WriterException,
			UnsupportedEncodingException {
		QRCodeWriter writer = new QRCodeWriter();
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

		matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,
				300, 300, hints);

		int width = matrix.getWidth();
		int hight = matrix.getHeight();

		int[] pixels = new int[width * hight];
		for (int y = 0; y < hight; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}
		Bitmap map = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
		map.setPixels(pixels, 0, width, 0, 0, width, hight);

		return map;

	}
}
