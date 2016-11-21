package com.haifeiWu.test;

import com.haifeiWu.utils.ReadConfFile;

public class testMain {
	public static void main(String[] args) {
		System.out.println("串口号："+ReadConfFile.getString("port"));
		System.out.println("网络接口："+ReadConfFile.getString("requestInterface"));
	}
}
