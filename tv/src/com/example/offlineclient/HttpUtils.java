package com.example.offlineclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

	public static String get(String url) {
		URL u;
		String result = "";
		try {
			u = new URL(url);
			InputStream in = u.openStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				byte buf[] = new byte[1024];
				int read = 0;
				while ((read = in.read(buf)) > 0) {
					out.write(buf, 0, read);
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
			byte b[] = out.toByteArray();
			result = new String(b, "utf-8");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String post(String url) {
		return null;
	}
}
