package com.haifeiWu.Piread;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;  
import java.io.InputStream;  
import java.util.Date;
import java.util.TooManyListenersException;  


public class SerialRead {
	public String retValue;
	public String data;
	public boolean isEmpty = true;
	public static Date nowTime = new Date();//当前时间
	public static Date startTime = new Date();
	public static boolean isActive = false;//是否被触发
	public static boolean isFirstAction = true;
	public static String oldDeviceId = null;
	public static String requestInterface = "http://192.168.1.172:8080/ManagePlantfromNew/readRfid.action";//与服务器的请求接口
	private String serialPort = "/dev/ttyUSB0";
	
	
	public void init() {  
	        try {  
	            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(serialPort);  
	            // 直接取得COM3端口  
	            System.out.println(portId.getName() + ":开启");  
	            @SuppressWarnings("unused")  
	            Read SerialRead = new Read(portId);
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	            System.out.println(ex.getMessage());
	        }  
	    }  
	 class Read implements  SerialPortEventListener {
	        InputStream inputStream;  
	        SerialPort serialPort;  
	        Thread readThread;  
	        public Read(CommPortIdentifier portId) throws InterruptedException {  
	            try {  
	                serialPort = (SerialPort) portId.open("MyReader", 2000);  
	                //portId.open("串口所有者名称", 超时等待时间);  
	            } catch (PortInUseException e) {  
	                //如果端口被占用就抛出这个异常  
	                e.printStackTrace();
	                System.out.println(e.getMessage());
	            }  
	  
	            try {  
	                inputStream = serialPort.getInputStream();  
	                //从COM3获取数据      
	            } catch (IOException e) {  
	            	System.out.println(e.getMessage());
	            }  
	  
	            try {  
	                serialPort.addEventListener(this);  
	                //添加监听器  
	            } catch (TooManyListenersException e) {  
	            }  
	  
	            serialPort.notifyOnDataAvailable(true);  
	            /* 
	             * 侦听到串口有数据,触发串口事件 
	             */  
	            try {  
	                serialPort.setSerialPortParams(9600,//波特率  
	                        SerialPort.DATABITS_8,//数据位数  
	                        SerialPort.STOPBITS_1,//停止位  
	                        SerialPort.PARITY_NONE);//校验  
	            } catch (UnsupportedCommOperationException e) {  
	            }  
	        }  
	  
	  
	        /** 
	         * BI -通讯中断. CD -载波检测. CTS -清除发送. DATA_AVAILABLE -有数据到达. DSR -数据设备准备好. 
	         * FE -帧错误. OE -溢位错误. OUTPUT_BUFFER_EMPTY -输出缓冲区已清空. PE -奇偶校验错. RI - 
	         * 振铃指示. 一般最常用的就是DATA_AVAILABLE--串口有数据到达事件。 
	         */  
	        public void serialEvent(SerialPortEvent event) {  
	  
	            switch (event.getEventType()) {  
	                case SerialPortEvent.BI:  
	                case SerialPortEvent.OE:  
	                case SerialPortEvent.FE:  
	                case SerialPortEvent.PE:  
	                case SerialPortEvent.CD:  
	                case SerialPortEvent.CTS:  
	                case SerialPortEvent.DSR:  
	                case SerialPortEvent.RI:  
	                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:  
	                    break;  
	                case SerialPortEvent.DATA_AVAILABLE:  
	                      try {  
		                    	
		                    	byte[]tt=new byte[17];//测试用的事前知道有15个字节码
		                        while(inputStream.available() > 0) {
		                        	for(int i=0;i<tt.length;i++){
		                        		tt[i]=(byte)inputStream.read();
	                        	    }
		                        }
		                        if(tt.length>=17){
		                        	data = bytesToHexString(tt);
		                        	System.out.println("数据data: "+data);
		                        	String oldData = null;

		                        	String deviceId = data.substring(2,4);
		                        	String wristId = data.substring(4,28);
		                        	String txID = data.substring(28,30);
		                        	String checkSum = data.substring(30,32);
		                        	oldDeviceId = deviceId;
		                        	String requestStr = "deviceId="+ deviceId+"&wristId="+wristId+"&txID="+txID;
		                        	//请求服务器，向服务器发送设备信息
		                        	//if(((new Date().getTime())-SerialRead.startTime.getTime())/1000>10){		                        	
			                            String s = HttpRequest.sendPost(SerialRead.requestInterface,requestStr );
			                            System.out.println(s);		                        	
//		                        	}
		                        }else{
		                        	System.out.println("没有设备！");
		                        }
//		                        Thread.sleep(3000);
		                    } catch (Exception e) {  
		                    	System.out.println(e.getMessage());
		                    }  
							
	                    break;    
	            }  
	        }  
	    }  
		
		 /**
		 * 把字节数组转换成16进制字符串
		 * @param bArray 需要被转换的byte数组
		 * @return 转换成16进制字符串
		 */
		public static String bytesToHexString(byte[] bArray) {
			StringBuffer sb = new StringBuffer(bArray.length);
			String sTemp;
			for (int i = 0; i < bArray.length; i++) {
				sTemp = Integer.toHexString(0xFF & bArray[i]);
				if (sTemp.length() < 2)
					sb.append(0);
				sb.append(sTemp.toUpperCase());
			}
			return sb.toString();
		}
	  
	    public static void main(String[] args)throws InterruptedException {  
	    	String requestStr = null;
	    	SerialRead reader = new SerialRead();  
	    	
	    	reader.init(); 
	        
	        while(true){//
	        	requestStr = "deviceId="+SerialRead.oldDeviceId+"&wristId=FFFFFFFF&txID=00";
	        	System.out.println(requestStr);
	        	if(SerialRead.isActive == true){
	        		SerialRead.isActive = false;//将该激活码设置为false
	        		SerialRead.startTime = new Date();//设置事件为当前时间
	        		SerialRead.nowTime = new Date();//更新当前时间
	        		System.out.println("SerialRead.isActive == true");
	        	}else if(SerialRead.isFirstAction == false){//当事件未被触发时
	        		System.out.println("SerialRead.isActive == false");
	        		if(((new Date().getTime())-SerialRead.startTime.getTime())/1000>60){
	        			//发送停止录像的请求信息
	        			HttpRequest.sendPost(SerialRead.requestInterface, "requestStr");
	        			SerialRead.startTime = new Date();//更新时间为当前时间
	        			System.out.println("超时……");
	        		}
	        	}
	        Thread.sleep(1000);//延时触发
	        }
	    }  
}
