package com.common.interfaces;

import com.common.network.FProtocol;

/**
 * 
 * @author songxudong
 *
 */
public interface IResponseCallBack {

	public void resultDataSuccess(int requestCode, String data);

	public void resultDataMistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus responseStatus, String errorMessage);
}
