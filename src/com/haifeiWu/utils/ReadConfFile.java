package com.haifeiWu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadConfFile {
	
	private ReadConfFile(){
		
	}
	
	 //私有化的内部类
    private static class SingletonInstance{
        static ReadConfFile instance = new ReadConfFile();
    }
	//获取ReadConfFile的单例对象
    public static ReadConfFile getInstance(){
        return SingletonInstance.instance;
    }
    /**
     * 获取参数
     * @param param 参数的key
     * @return 参数的value
     */
	public static String getString(String param){
		return getInstance().getPropertiesString("/conf.properties", param);
	}
	/**
	 * 获取Peoperties文件的参数的值
	 * @param path 路径
	 * @param param 参数的key
	 * @return 参数的value
	 */
	public String getPropertiesString(String path,String param){
		Properties prop =  new  Properties();
        InputStream in = this.getClass().getResourceAsStream(path);    
        try {
			prop.load(in);
		} catch (IOException e) {
			throw new RuntimeException();
		}    
        return prop.getProperty(param);   
	}
}
