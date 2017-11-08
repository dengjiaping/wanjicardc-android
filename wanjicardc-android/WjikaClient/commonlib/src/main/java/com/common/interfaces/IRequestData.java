package com.common.interfaces;

import com.common.network.FProtocol;
import com.common.network.IResponseJudger;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author songxudong
 */
public interface IRequestData {
    public void requestHttpData(String path, int requestCode);

    public void requestHttpData(String path, int requestCode, IResponseJudger judger);

    public void requestHttpData(String path, int requestCode, FProtocol.NetDataProtocol.DataMode dataAccessMode);

    public void requestHttpData(String path, int requestCode, FProtocol.NetDataProtocol.DataMode dataAccessMode, IResponseJudger judger);

    public void requestHttpData(String path, int requestCode,
                                FProtocol.HttpMethod method, IdentityHashMap<String, String> postParameters);

    public void requestHttpData(String path, int requestCode, FProtocol.HttpMethod method,
                                IdentityHashMap<String, String> postParameters, IResponseJudger judger);

    public void requestHttpData(String path, int requestCode,
                                FProtocol.NetDataProtocol.DataMode dataAccessMode, FProtocol.HttpMethod method,
                                IdentityHashMap<String, String> postParameters);

    public void requestHttpData(String path, int requestCode,
                                FProtocol.NetDataProtocol.DataMode dataAccessMode, FProtocol.HttpMethod method,
                                IdentityHashMap<String, String> postParameters, IResponseJudger judger);


}
