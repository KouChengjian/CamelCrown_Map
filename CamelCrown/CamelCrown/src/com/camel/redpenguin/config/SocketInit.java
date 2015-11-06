package com.camel.redpenguin.config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Iterator;
import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.bean.Packet;
import com.camel.redpenguin.listener.EventListener;
import com.camel.redpenguin.listener.FindFamilyListener;
import com.camel.redpenguin.listener.FindListener;
import com.camel.redpenguin.listener.FindNewMessageListener;
import com.camel.redpenguin.listener.FindStateListener;
import com.camel.redpenguin.listener.HeartbeatHzListener;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.util.DecodeUtils;
import com.camel.redpenguin.util.EncodeUtils;
import com.camel.redpenguin.util.NetworkUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * @ClassName: SocketInit
 * @Description: socket初始化
 * @author kcj
 * @date
 */
public class SocketInit {

	private static SocketInit mInstance = null;

	private static Context mContext = CustomApplcation.getInstance();
	
	private final int STATE_OPEN = 1;// socket打开
	private final int STATE_CLOSE = 1 << 1;// socket关闭
	private final int STATE_CONNECT_START = 1 << 2;// 开始连接server
	private final int STATE_CONNECT_SUCCESS = 1 << 3;// 连接成功
	//private final int STATE_CONNECT_FAILED = 1 << 4;// 连接失败
	//private final int STATE_CONNECT_WAIT = 1 << 5;// 等待连接

	private String IP = "";
	private int PORT = 0;
	private int state = STATE_CONNECT_START;

	Selector selector;
	ByteBuffer readBuffer = ByteBuffer.allocate(8192);// 8192
	SocketChannel socketChannel;

	private Thread conn = null;

	private ArrayList<Packet> requestQueen = new ArrayList<Packet>();
	private final Object lock = new Object();
	private final String TAG = "NioClient";
	
	public int socketState(){
		return state;
	}

	public SocketInit(Context context,String host, int port) {
		this.IP = host;
		this.PORT = port;
		open();
	}

    public boolean isCanSend(){
    	if(!NetworkUtil.isNetworkAvailable(mContext)){
    		Log.e("Socket","无网络，请链接...");
			ShowToast("无网络，请链接...");
			return false;
		}
		if(!SocketInit.getInstance().isSocketConnected()){
			Log.e("Socket","后台服务正在维护中，请稍后连接...");
			ShowToast("后台服务正在维护中，请稍后连接...");
			reconn();
			return false;
		}
		return true;
	}
	
	/**
	 * 发送
	 */
	public int send(Packet in) {
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 注册 - 1F
	 */
	public int register(Packet in ,SaveListener saveListener) {
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 登入 - 2F
	 */
	public int login (String name ,String password ,SaveListener saveListener) {
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		String string = EncodeUtils.loginDataEncode(name , password);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 查询账号关联设备  - 3A
	 */
	public int queryCurrentContactList(FindListener findListener) {
		if(!isCanSend()){
			return -1;
		}
		EventListener.findListener = findListener;
		String str = EncodeUtils.queryDeviceDataEncode(CustomApplcation.getInstance().getUsername());
		final Packet in = new Packet();
		in.pack(str);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 查询是否为管理员 - 3B
	 */
	public int isQueryAdministrator (SaveListener saveListener){
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		String string = EncodeUtils.queryIsAdministratorDataEncode(Config.DEVICE_IDENTIFY);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 添加设备 - 2A
	 */
	public int addDevice(SaveListener saveListener){
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		String string = EncodeUtils.addDeviceDataEncode(Config.DEVICE_IDENTIFY);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 删除设备(取消关注) - 3C
	 */
	public int cancelDevice(String identify ,SaveListener saveListener){
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		String string = EncodeUtils.cancelDeviceDataEncode(identify);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 选择当前设备 - 1B
	 */
	public int selectorDevice(String selectorPosition ,SaveListener saveListener){
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		String string = EncodeUtils.selectorDeviceDataEncode(selectorPosition);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 实时定位 - 1E
	 */
	public int realTimeLocation(SaveListener saveListener){
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return -1;
		}
		EventListener.saveListener = saveListener;
		String identify = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		String string = EncodeUtils.realTimeLocationModeDataEncode(identify);
		//String string = EncodeUtils.realTimeLocationModeDataEncode("1503130002");
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 定位频率(安全，省电，紧急) - 1D
	 */
	public int locationMode(Packet in, SaveListener saveListener) {
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}

	/**
	 *  本地心跳
	 */
	public void heartbeatHz(){
		if(!isCanSend()){
			return;
		}
	}
			
	/**
	 * 心跳 - 1C
	 */
	public int heartbeatHz(Packet in ,HeartbeatHzListener heartbeatHzListener) {
		if(!isCanSend()){
			return -1;
		}
		EventListener.heartbeatHzListener = heartbeatHzListener;
		String string = EncodeUtils.heartbeatRateDataEncode();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}

	/**
	 * 查询账号关联设备 (家庭成员) - 3D
	 */
	public int queryCurrentFamilyMember(FindFamilyListener findFamilyListener){
		if(!isCanSend()){
			return -1;
		}
		EventListener.findFamilyListener = findFamilyListener;
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		String string = EncodeUtils.queryFamilyDataEncode(id);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 查询指定设备的状态信息 - 3E
	 */
	@SuppressLint("SimpleDateFormat")
	public int queryCurrentDeviceState(int page , String time, FindStateListener findStateListener){
		if(!isCanSend()){
			return -1;
		}
		EventListener.findStateListener = findStateListener;
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		String string = EncodeUtils.querydDeviceDataEncode(id,time,page);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 重设密码  - 3F
	 */
	public int anewSetPwd(String acc ,String pwd ,SaveListener saveListener){
		if(!isCanSend()){
			return -1;
		}
		EventListener.saveListener = saveListener;
		String string = EncodeUtils.amendPasswordDataEncode(acc,pwd);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 获取当前设备最新数据 - 4A
	 */
	public int queryCurrentNewestMessage(FindNewMessageListener findNewMessageListener){
		if(!isCanSend()){
			return -1;
		}
		String identify = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		EventListener.findNewMessageListener = findNewMessageListener;
		String string = EncodeUtils.newestMessageDataEncode(identify);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 获取指定历史记录长度 - 4B
	 */
	public int queryHistoryLength( String time , SaveListener saveListener){
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return -1;
		}
		String identify = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		EventListener.saveListener = saveListener;
		String string = EncodeUtils.queryLengthDataEncode(identify, time);
		Packet in = new Packet();
		in.pack(string);
		synchronized (lock) {
			requestQueen.add(in);
		}
		this.selector.wakeup();
		return in.getId();
	}
	
	/**
	 * 取消
	 */
	public void cancel(int reqId) {
		Iterator<Packet> mIterator = requestQueen.iterator();
		while (mIterator.hasNext()) {
			Packet packet = mIterator.next();
			if (packet.getId() == reqId) {
				mIterator.remove();
			}
		}
	}

	public boolean isSocketConnected() {
		return ((state == STATE_CONNECT_SUCCESS) && (null != conn && conn
				.isAlive()));
	}

	/**
	 * 打开
	 */
	public void open() {
		reconn();
	}

	public void open(String host, int port) {
		this.IP = host;
		this.PORT = port;
		reconn();
	}

	private long lastConnTime = 0;

	/**
	 * 重新连接
	 */
	public synchronized void reconn() {
		if(!NetworkUtil.isNetworkAvailable(mContext)){
    		Log.e("Socket","无网络，请链接...");
			ShowToast("无网络，请链接...");
			return ;
		}
		if (System.currentTimeMillis() - lastConnTime < 2000) {
			return;
		}
		lastConnTime = System.currentTimeMillis();

		close();
		state = STATE_OPEN;
		conn = new Thread(new Conn());
		conn.start();
	}

	/**
	 * 关闭
	 */
	public synchronized void close() {
		try {
			if (state != STATE_CLOSE) {
				try {
					if (null != conn && conn.isAlive()) {
						conn.interrupt();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn = null;
				}
				state = STATE_CLOSE;
			}
			requestQueen.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接
	 */
	private class Conn implements Runnable {
		public void run() {
			Log.e(TAG, "Conn :Start");
			try {
				state = STATE_CONNECT_START; // 状态连接开始
				selector = SelectorProvider.provider().openSelector();
				socketChannel = SocketChannel.open(); // 获得一个Socket通道    
				socketChannel.configureBlocking(false); // 设置通道为非阻塞 
				InetSocketAddress address = new InetSocketAddress(IP, PORT);
				socketChannel.connect(address);
				//将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。 
				socketChannel.register(selector, SelectionKey.OP_CONNECT);

				while (state != STATE_CLOSE) { // 状态不为关闭
					Log.e(TAG, "Conn :-------");
					selector.select();
					 // 获得selector中选中的项的迭代器  
					Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
					while (selectedKeys.hasNext()) {
						SelectionKey key = (SelectionKey) selectedKeys.next();
						// 删除已选的key,以防重复处理  
						selectedKeys.remove();
						if (!key.isValid()) {
							continue;
						}
						// 连接事件发生  
						if (key.isConnectable()) {
							finishConnection(key); // 结束连接
						} else if (key.isReadable()) {
							read(key); // 读取数据
						} else if (key.isWritable()) {
							write(key); // 发送数据
						}
					}

					synchronized (lock) {
						if (requestQueen.size() > 0) {
							SelectionKey key = socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
				}

			} catch (Exception e) {
				close();
				e.printStackTrace();
				Log.e(TAG, e.toString());
				Log.e(TAG, "连接断开....");
			} finally {
				if (null != socketChannel) {
					socketChannel.keyFor(selector).cancel();
					try {
						socketChannel.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			Log.i(TAG, "Conn :End");
		}

		private boolean finishConnection(SelectionKey key) throws IOException {
			boolean result = false;
			SocketChannel socketChannel = (SocketChannel) key.channel();
			result = socketChannel.finishConnect();// 没有网络的时候也返回true
			if (result) {
				key.interestOps(SelectionKey.OP_READ);
				state = STATE_CONNECT_SUCCESS;
			}
			Log.v(TAG, "finishConnection :" + result);
			return result;
		}

		private void read(SelectionKey key) throws IOException {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			readBuffer.clear();
			int numRead;
			numRead = socketChannel.read(readBuffer);
			if (numRead == -1) {
				key.channel().close();
				key.cancel();
				return;
			}
//			Log.e("接收", String.valueOf(numRead));
			Log.v("接收", new String(readBuffer.array(), 0,numRead));
			//Log.e("接收", new String(readBuffer.array(), "UTF-8"));
			//Log.e("接收", new String(readBuffer.array(), "GB2312"));
			//Log.e("接收", new String(readBuffer.array(), "GBK"));
			//Log.e("接收", new String(readBuffer.array(), "ISO-8859-1"));
			DecodeUtils.decodeData(new String(readBuffer.array(), 0,numRead));
			/*Intent intent = new Intent();
			intent.setAction("action.main.refresh.3a");
			mContext.sendBroadcast(intent);*/
		}

		private void write(SelectionKey key) throws IOException {

			SocketChannel socketChannel = (SocketChannel) key.channel();
			synchronized (lock) {
				Packet item = null;
				Iterator<Packet> iter = requestQueen.iterator();
				while (iter.hasNext()) {
					item = iter.next();
					ByteBuffer buf = ByteBuffer.wrap(item.getPacket());
					socketChannel.write(buf);
					iter.remove();
				}
				Log.v("发送", new String(item.getPacket(), "UTF-8"));
				item = null;
			}
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	public static void init(Context context, String SERVERIP, int SERVERPORT) {
		if (mInstance == null) {
			mInstance = new SocketInit(context, SERVERIP, SERVERPORT);
		}
	}

	public static SocketInit getInstance() {
		return mInstance;
	}
	
	/**
	 * 显示Toast
	 */
	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			Toast.makeText(mContext, text,Toast.LENGTH_LONG).show();
		}
	}
}
