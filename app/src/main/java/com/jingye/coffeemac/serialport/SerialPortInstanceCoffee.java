package com.jingye.coffeemac.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.jingye.coffeemac.common.action.TViewWatcher;
import com.jingye.coffeemac.service.ITranCode;
import com.jingye.coffeemac.service.Remote;
import com.jingye.coffeemac.util.log.LogUtil;

public class SerialPortInstanceCoffee {

	private static SerialPortInstanceCoffee instance = null;

	private SerialPort mSerialPort = null;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private CoffeeReadThread mReadThread;

	private char[] readBufferToChar;
	private String receivedData = "";

	private SerialPortInstanceCoffee() {
	}

	public static SerialPortInstanceCoffee getInstance() {
		if (instance == null) {
			synchronized (SerialPortInstanceCoffee.class) {
				if (instance == null) {
					instance = new SerialPortInstanceCoffee();
					instance.initSerialPort();
				}
			}
		}
		return instance;
	}

	private void initSerialPort() {
		try {
			readBufferToChar = new char[2048];
			getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new CoffeeReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
	}

	public OutputStream getOutputStream() {
		return mOutputStream;
	}

	private SerialPort getSerialPort() throws SecurityException, IOException,
			InvalidParameterException {
		if (mSerialPort == null) {
			String path = "/dev/ttyS2";
//			String path = "/dev/ttyS3";
//			String path = "/dev/ttyUSB0";
//			String path = "/dev/ttyUSB1";
			int baudrate = 9600;
			if ((path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}

	private void processReceivedData(final byte[] buffer, final int size) {
		for (int i = 0; i < size; i++) {
			readBufferToChar[i] = (char) buffer[i];
		}
		char[] ch = String.copyValueOf(readBufferToChar, 0, size).toCharArray();
		String temp;
		StringBuilder tmpSB = new StringBuilder();
		for (int i = 0; i < ch.length; i++) {
			temp = String.format("%02x", (int) ch[i]);
			if (temp.length() == 4) {
				tmpSB.append(temp.substring(2, 4));
			} else {
				tmpSB.append(temp);
			}
		}
		temp = tmpSB.toString();
		LogUtil.vendor("temp = " + temp);

		if (temp.startsWith("aa")) {
			receivedData = "" + temp;
			if(temp.contains("ee")){
				String result = receivedData.substring(0, receivedData.indexOf("ee") + 2);
				if(result.length() < receivedData.length()){
					if(result.length()%2 == 1 && receivedData.substring(receivedData.indexOf("ee") + 2, receivedData.indexOf("ee") + 3).equals("e")){
						result += "e";
					}else if((receivedData.length() - result.length()) >= 2 && result.length()%2 == 0 && receivedData.substring(receivedData.indexOf("ee") + 2,
							receivedData.indexOf("ee") + 4).equals("ee")){
						result += "ee";
					}
				}

				dispatchResult(result);
			}

		} else if (temp.contains("ee")) {
			receivedData += temp;
			String result = receivedData.substring(0, receivedData.indexOf("ee") + 2);
			if(result.length() < receivedData.length()){
				if(result.length()%2 == 1 && receivedData.substring(receivedData.indexOf("ee") + 2, receivedData.indexOf("ee") + 3).equals("e")){
					result += "e";
				}else if((receivedData.length() - result.length()) >= 2 && result.length()%2 == 0 && receivedData.substring(receivedData.indexOf("ee") + 2,
						receivedData.indexOf("ee") + 4).equals("ee")){
					result += "ee";
				}
			}

			dispatchResult(result);

		} else if (!receivedData.equals("")) {
			receivedData += temp;
		}
	}

    private void dispatchResult(String result){
        LogUtil.vendor("result = " + result);
        if ((result.length() == 16 || result.length() == 14)
				&& result.substring(4, 6).equals("30")) {
            // 设置温度指令结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT, ITranCode.ACT_COFFEE_SERIAL_PORT_SYNC);
        }else if ((result.length() == 16 || result.length() == 14)
				&& result.substring(4, 6).equals("46")) {
			// 打混合饮料结果
			processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT, ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE);
		}else if ((result.length() == 16 || result.length() == 14)
				&& result.substring(4, 6).equals("38")) {
			// 获取温度结果
			processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT, ITranCode.ACT_COFFEE_SERIAL_PORT_TEMP_GET);
		}else if (result.length() == 14
				&& result.substring(4, 6).equals("21")) {
			// 落杯结果
			processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT, ITranCode.ACT_COFFEE_SERIAL_PORT_CUP_DROP);
		}else if (result.length() == 14
				&& result.substring(4, 6).equals("23")) {
			// 杯筒转动结果
			processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT, ITranCode.ACT_COFFEE_SERIAL_PORT_CUP_TURN);
		}else if ((result.length() == 16 || result.length() == 14)
				&& result.substring(4, 6).equals("0A")) {
			// 全检结果
			processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT, ITranCode.ACT_COFFEE_SERIAL_PORT_CHECK);
		}else if ((result.length() == 16 || result.length() == 14)
				&& result.substring(4, 6).equals("35")) {
			// 清洗结果
			processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT, ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING);
		}else if((result.length() == 14 )
				&& result.substring(4, 6).equals("60")){
			//检测有杯结果
			processResult(result,ITranCode.ACT_COFFEE_SERIAL_PORT,ITranCode.ACT_COFFEE_SERIAL_PORT_CHECK_CUP);
		}else if((result.length() == 16 )&&(result.substring(4, 6).equals("61"))){
			processResult(result,ITranCode.ACT_COFFEE_SERIAL_PORT,ITranCode.ACT_COFFEE_SERIAL_PORT_GET_MACHINE_TYPE);
		}
    }

	private void processResult(String result, int what, int action) {
		LogUtil.vendor("processResult -> " + result);

		Remote remote = new Remote();
		remote.setWhat(what);
		remote.setAction(action);
		remote.setBody(result);
		TViewWatcher.newInstance().notifyAll(remote);

		if (receivedData.length() > result.length()) {
			receivedData = receivedData.substring(result.length());
		} else {
			receivedData = "";
		}
	}

	private void closeSerialPort() {
		if (mSerialPort != null) {
	 		mSerialPort.close();
	 		mSerialPort = null;
	 	}
	}

	private class CoffeeReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				LogUtil.e("DEBUG", "[COFFEE]Thread name is " + getName());
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						processReceivedData(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
}
