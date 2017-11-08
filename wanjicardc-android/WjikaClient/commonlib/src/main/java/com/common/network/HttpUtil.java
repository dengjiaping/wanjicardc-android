package com.common.network;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.common.utils.CheckRootUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.EnCryptionUtils;
import com.common.utils.LogUtil;
import com.common.utils.StringUtil;
import com.common.utils.VersionInfoUtils;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okio.Buffer;

/**
 * Created by jacktian on 15/8/19.
 */
public class HttpUtil {

    public static final String USER_AGENT = "WJK Android Client v2.0";
    public static final String MULTIPART_DATA_NAME = "file";
    public static final String TAG = HttpUtil.class.getSimpleName();

    static OkHttpClient client;


    /**
     * get方式
     *
     * @param url
     * @return
     */
    public static String httpGet(Context context, String url) {
        IdentityHashMap<String, String> params = new IdentityHashMap<>();

        if (url != null && url.contains("?")) {
            Uri uri = Uri.parse(url);
            String uriScheme = uri.getScheme();
            String uriAuthority = uri.getAuthority();
            String uriPath = uri.getPath();

            String finalPath = Config.INTERFACE_PATH;
            for (String key : uri.getQueryParameterNames()) {
                //斗金需要特殊处理
                if ("dj".equals(key)) {
                    finalPath = "/card";
                } else {
                    params.put(key, uri.getQueryParameter(key));
                }
            }
            params = securityCheckParams(context, params, uriPath);
            url = uriScheme + "://" + uriAuthority + finalPath + "?" +
                    String.format("%s=%s", Config.INTERFACE_NAME, params.get(Config.INTERFACE_NAME));
            if (LogUtil.isDebug) Log.e(TAG, url);

            Request request = new Request.Builder()
                    .addHeader("User-Agent", USER_AGENT)
                    .url(url)
                    .build();
            Response response;
            try {
                response = getCustomClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                LogUtil.e(TAG, e.toString());
            }
        }
        return "";
    }

    /**
     * get方式
     *
     * @param url
     * @return
     */
    public static String httpGet(String url, String lastModified, Context context) {
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        if (url != null && url.contains("?")) {
            Uri uri = Uri.parse(url);
            String uriScheme = uri.getScheme();
            String uriAuthority = uri.getAuthority();
            String uriPath = uri.getPath();
            for (String key : uri.getQueryParameterNames()) {

                params.put(key, uri.getQueryParameter(key));
            }
            params = securityCheckParams(context, params, uriPath);
            url = uriScheme + "://" + uriAuthority + Config.INTERFACE_PATH
                    + "?" + String.format("%s=%s", Config.INTERFACE_NAME, params.get(Config.INTERFACE_NAME));

            Request request = new Request.Builder()
                    .addHeader("User-Agent", USER_AGENT)
                    .addHeader("If-Modified-Since", lastModified)
                    .url(url)
                    .build();

            Response response;
            try {
                response = getCustomClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    LastModified.saveLastModified(context, url.hashCode() + "", response.header("Last-Modified"));
                    return response.body().string();
                }
            } catch (IOException e) {
                LogUtil.e(TAG, e.toString());
            }
        }
        return "";
    }

    /**
     * post方式
     *
     * @param url
     * @param postParameters
     * @return
     */
    public static String httpPost(Context context, String url, IdentityHashMap<String, String> postParameters) {
        try {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            if (postParameters == null) {
                postParameters = new IdentityHashMap<>();
            }
            String finalPath = Config.INTERFACE_PATH;
            //斗金需要特殊处理
            if (postParameters.containsKey("dj")) {
                finalPath = "/card";
                postParameters.remove("dj");
            }
            if (url != null) {
                Uri uri = Uri.parse(url);
                String uriScheme = uri.getScheme();
                String uriAuthority = uri.getAuthority();
                String uriPath = uri.getPath();

                for (String key : uri.getQueryParameterNames()) {
                    postParameters.put(key, uri.getQueryParameter(key));
                }
                postParameters = securityCheckParams(context, postParameters, uriPath);
                url = uriScheme + "://" + uriAuthority + finalPath;
                if (LogUtil.isDebug) Log.e(TAG, url);
                Set<String> set = postParameters.keySet();
                for (String key : set) {
                    builder.add(key, postParameters.get(key));
                }
                RequestBody formBody = builder.build();
                Request request = new Request.Builder()
                        .addHeader("User-Agent", USER_AGENT)
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = getCustomClient().newCall(request).execute();

                if (response.isSuccessful()) {
                    return response.body().string();
                }
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "request error " + e.toString());
        } finally {
        }
        return "";
    }

    /**
     * post方式传输文件
     *
     * @param context
     * @param url
     * @param postParameters
     * @param file
     * @return
     */
    public static String httpPost(Context context, String url, IdentityHashMap<String, String> postParameters, String streamName, File file) {
        try {
            if (file.exists() && file.isFile() && url != null) {
                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                String type = fileNameMap.getContentTypeFor(file.getAbsolutePath());
//                Uri uri = Uri.parse(url);
//                String uriScheme = uri.getScheme();
//                String uriAuthority = uri.getAuthority();
//                String uriPath = uri.getPath();
                postParameters = securityCheckParamsSign(context, postParameters);
//                url = uriScheme + "://" + uriAuthority + "/";
                Set<String> set = postParameters.keySet();
                MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
                for (String key : set) {
                    multipartBuilder.addFormDataPart(key, postParameters.get(key));
                }
                if (StringUtil.isEmpty(streamName)) {
                    streamName = MULTIPART_DATA_NAME;
                }
                multipartBuilder.addFormDataPart(streamName, file.getName(), RequestBody.create(MediaType.parse(type), file));
                RequestBody requestBody = multipartBuilder.build();
                Request request = new Request.Builder()
                        .addHeader("User-Agent", USER_AGENT)
                        .url(url)
                        .post(requestBody)
                        .build();
                Response response = getCustomClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * put方式
     *
     * @param url
     * @param postParameters
     * @return
     */
    public static String httpPut(Context context, String url, IdentityHashMap<String, String> postParameters) {
        try {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            if (postParameters == null) {
                postParameters = new IdentityHashMap<String, String>();
            }
            if (url != null && url.contains("?")) {
                Uri uri = Uri.parse(url);
                String uriScheme = uri.getScheme();
                String uriAuthority = uri.getAuthority();
                String uriPath = uri.getPath();
                for (String key : uri.getQueryParameterNames()) {

                    postParameters.put(key, uri.getQueryParameter(key));
                }
                url = uriScheme + "://" + uriAuthority;
                postParameters = securityCheckParams(context, postParameters, uriPath);
                Set<String> set = postParameters.keySet();
                for (String key : set) {
                    builder.add(key, Uri.encode(postParameters.get(key), "UTF-8"));
                }
                RequestBody formBody = builder.build();
                Request request = new Request.Builder()
                        .addHeader("User-Agent", USER_AGENT)
                        .url(url)
                        .put(formBody)
                        .build();

                Response response = getCustomClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            }
        } catch (Exception e) {
            LogUtil.e("HttpUtil", e.toString());
        } finally {
        }
        return "";

    }

    /**
     * delete方式
     *
     * @param url
     * @return
     */
    public static String httpDelete(Context context, String url, IdentityHashMap<String, String> postParameters) {
        try {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            if (postParameters == null) {
                postParameters = new IdentityHashMap<String, String>();
            }
            if (url != null && url.contains("?")) {
                Uri uri = Uri.parse(url);
                String uriScheme = uri.getScheme();
                String uriAuthority = uri.getAuthority();
                String uriPath = uri.getPath();
                for (String key : uri.getQueryParameterNames()) {

                    postParameters.put(key, uri.getQueryParameter(key));
                }
                url = uriScheme + "://" + uriAuthority;
                postParameters = securityCheckParams(context, postParameters, uriPath);
                Set<String> set = postParameters.keySet();
                for (String key : set) {
                    builder.add(key, Uri.encode(postParameters.get(key), "UTF-8"));
                }
                RequestBody formBody = builder.build();
                Request request = new Request.Builder()
                        .addHeader("User-Agent", USER_AGENT)
                        .url(url)
                        .method("DELETE", formBody)
                        .build();

                Response response = getCustomClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            }
        } catch (Exception e) {
            LogUtil.e("HttpUtil", e.toString());
        } finally {
        }
        return "";

    }


    /**
     * 数据安全检查
     *
     * @param params
     * @return
     */
    private static IdentityHashMap<String, String> securityCheckParams(Context context, IdentityHashMap<String, String> params, String method) {
        if (params == null)
            params = new IdentityHashMap<>();

        params.put("buildModel", Build.MODEL);
        params.put("appType", Config.APP_TYPE);
        params.put("deviceId", getDeviceInfo(context));
        params.put("deviceVersion", Build.VERSION.RELEASE);
        params.put("appVersion", VersionInfoUtils.getVersion(context));
//        params.put("app_id", Config.APP_ID);
        IdentityHashMap<String, String> newParam = new IdentityHashMap<>();
        newParam.put(Config.INTERFACE_NAME, getSidParam(params, method));
        return newParam;
    }

    /**
     * 数据安全检查
     *
     * @param params
     * @return
     */
    private static IdentityHashMap<String, String> securityCheckParamsSign(Context context, IdentityHashMap<String, String> params) {
        if (params == null)
            params = new IdentityHashMap<>();

        params.put("buildModel", Build.MODEL);
        params.put("appType", Config.APP_TYPE);
        params.put("deviceId", getDeviceInfo(context));
        params.put("deviceVersion", Build.VERSION.RELEASE);
        params.put("appVersion", VersionInfoUtils.getVersion(context));
        params.put("sign", getSignParam(params));
        params.put("sysVersion", Config.INTERFACE_VERSION);
        return params;
    }

    private static String getDeviceInfo(Context context) {
        StringBuilder deviceStr = new StringBuilder();
        deviceStr.append(CheckRootUtil.isDeviceRooted());
        deviceStr.append(",");
        deviceStr.append(DeviceUtil.getDeviceIdData(context));
        deviceStr.append(",");
        deviceStr.append(DeviceUtil.getAndroidId(context));
        deviceStr.append(",");
        deviceStr.append(DeviceUtil.getBluetoothMac(context));
        deviceStr.append(",");
        deviceStr.append(DeviceUtil.getBootSerialno(context));
        deviceStr.append(",");
        return deviceStr.toString();
    }

    private static String getSignParam(IdentityHashMap<String, String> params) {
        StringBuilder strParams = new StringBuilder();
        String result = "";
        //先将参数以其参数名的字典序升序进行排序
        //ksort($params);
        if (params != null) {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(params.entrySet());
            //排序

            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            //遍历排序后的参数数组中的每一个key/value对
            for (Map.Entry<String, String> entry : infoIds) {
                if (LogUtil.isDebug) Log.e(TAG, "getSignParam entry : " + entry);
                String value = entry.getValue();
                if (StringUtil.isEmpty(value)) {
                    value = "";
                }
                strParams.append(String.format("%s%s", entry.getKey(), Uri.encode(value, "UTF-8")));
            }


            try {
                if (LogUtil.isDebug)
                    Log.e(TAG, "getSignParam value : " + strParams.toString().toLowerCase());
                result = Base64.encodeToString(
                        EnCryptionUtils.hexString(EnCryptionUtils.eccryptSHA1(strParams.toString().toLowerCase())).getBytes(),
                        Base64.DEFAULT).trim();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
        if (LogUtil.isDebug) Log.e(TAG, "getSignParam result : " + result);
        return result;
    }

    private static String getSidParam(IdentityHashMap<String, String> params, String method) {
        StringBuilder str = new StringBuilder(); //待签名字符串
        StringBuilder strParams = new StringBuilder();
        String result = "";
        //先将参数以其参数名的字典序升序进行排序
        //ksort($params);
        if (params != null) {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(params.entrySet());
            //排序

            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            //遍历排序后的参数数组中的每一个key/value对
            for (Map.Entry<String, String> entry : infoIds) {
                if (LogUtil.isDebug) Log.e(TAG, "entry : " + entry);
                String value = entry.getValue();
                if (StringUtil.isEmpty(value)) {
                    value = "";
                }
                str.append(String.format("%s", Uri.encode(value.replace("-", "--"), "UTF-8")));
                str.append(".-.");
                strParams.append(String.format("%s%s", entry.getKey(), Uri.encode(value.replace("-", "--"), "UTF-8")));
            }

            if (LogUtil.isDebug) Log.e(TAG, "method : " + method);
            str.append(Config.INTERFACE_VERSION);
            if (LogUtil.isDebug) Log.e(TAG, str.toString());
            if (LogUtil.isDebug) Log.e(TAG, strParams.toString());
            StringBuilder sbResult = new StringBuilder();
            sbResult.append(Base64.encodeToString(str.toString().getBytes(), Base64.DEFAULT).trim());
            sbResult = sbResult.reverse();
            sbResult.append("=");

            sbResult.append(Base64.encodeToString(Config.INTERFACE_CODE.getBytes(), Base64.DEFAULT).trim());
            String interfacePath = Base64.encodeToString(method.getBytes(), Base64.DEFAULT).trim();
            StringBuilder sbPath = new StringBuilder(interfacePath);
            if (sbPath != null && sbPath.length() > 1) {

                sbPath.insert(1, 'w');
            }

            sbResult.append(sbPath);
            sbResult.append("=");
            try {
                sbResult.append(
                        Base64.encodeToString(EnCryptionUtils.hexString(EnCryptionUtils.eccryptSHA1(strParams.toString().toLowerCase())).getBytes(),
                                Base64.DEFAULT).trim());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            result = sbResult.toString();

        }
        return result;
    }

    /**
     * 初始化网络请求Client对象
     */
    private static OkHttpClient getCustomClient() {

        if (client == null) {
            synchronized (HttpUtil.class) {
                if (client == null) {
                    client = new OkHttpClient();

                    if (LogUtil.isDebug) {
                        client.networkInterceptors().add(new StethoInterceptor());
                    }
                    setCertificateForString();//双向
//                    client.setCertificatePinner(new CertificatePinner.Builder().add(Config.DOMAIN_NAME, "sha256/" + Config.HTTPS_SHA256).build());//单向
                    client.setConnectTimeout(10, TimeUnit.SECONDS);
                    client.setWriteTimeout(30, TimeUnit.SECONDS);
                    client.setReadTimeout(30, TimeUnit.SECONDS);
                    client.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            Log.e(TAG, "hostname : " + hostname + " session : " + session.getPeerHost() + " " + session.getProtocol()
                                    + " : " + session.getCipherSuite()
//                                    + " : " + session.getValueNames()[0]
                                    + (hostname.equals(session.getPeerHost())));
                            try {
                                for (Certificate certificate : session.getPeerCertificates()) {
                                    try {
                                        Log.e(TAG, "public key : " + certificate.getPublicKey()
                                                + " type : " + certificate.getType()
                                                + " SubjectAlternativeName : " + ((X509Certificate) certificate).getSubjectAlternativeNames()
                                                + " OID : " + ((X509Certificate) certificate).getSigAlgOID());
                                    } catch (CertificateParsingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (SSLPeerUnverifiedException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                }
            }

        }

        return client;
    }

    /**
     * 使用证书文件
     */
    public static void setCertificates(Context context, InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            //初始化keystore
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(context.getAssets().open("zhy_client.bks"), "123456".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, "123456".toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            getCustomClient().setSslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCertificateForString() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            InputStream certificate = new Buffer()
                    .writeUtf8(Config.HTTPS_SHA256)
                    .inputStream();
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

            try {
                if (certificate != null)
                    certificate.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException : " + e.toString());
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init(
                    null,
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom()
            );
            client.setSslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            Log.e(TAG, "e : " + e.toString());
        }
    }

    public void setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
            client.setSslSocketFactory(sslContext.getSocketFactory());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class Config {

        //接口客户端编号
        public static final String APP_TYPE = "2_2";
        //域名
        public static final String DOMAIN_NAME = "mobilecardc.wjika.com";
        // 接口版本
        public static final String INTERFACE_VERSION = "1.9";
        //接口编码
        public static final String INTERFACE_CODE = "000";
        //接口参数名称
        public static final String INTERFACE_NAME = "sid";
        //接口初始path
        public static final String INTERFACE_PATH = "/wjapp_mobile/card";
        //HTTPS证书的sha256加密串
        public static final String HTTPS_SHA256 =
                "MIIEogIBAAKCAQEA4F9ttRpd9ZAiErPGqwKAmAgX9miMROzrJDd9uZpBl9R1gOEP\n" +
                "1MXKhD+CzgVAHHjIOKVIEnea9G+YK9qcclD2Roxpum574qBQeBckAryc+dy6Nehi\n" +
                "JkBPugflxKff/RvzUReAAQPdooLbFVYnxLRllCT22otGXqVdIetEWwDkDnlyO3Q0\n" +
                "kA4M6hjaRyCFWRmjyuwZb4niRtUO+62KL7JlNP2gUKwQOJNzJX87U268FPiAWCFK\n" +
                "tmFhHAGr2WqsGpseXlmNY+h5YLIme8eQ9b6+5rmLdNDZY0BBERdUPoqs/Hp8s7Mv\n" +
                "vxbQaHFipb+3IojMLgNX+QXirCH17GzqchAtnwIDAQABAoIBAAxuXRaumkwTqz+c\n" +
                "BELPRa6mrCUzz6m18qU0Cdwxpraxx7zXO1hPiRPgrO+bLN0m9/jjd2XGiKhIUocD\n" +
                "eq1+vNZVyznQCz8YIL02kwEv0Aar6wk3kdCKuaAV9e36Wxeb59QzWB02SJ5LX7YD\n" +
                "6vokAIi/r4mWM9Tklo9diJD+MKR4jYGtkyAdqAzHzHPCro8NXry6MIPi7pMnW8r5\n" +
                "nSxUSB5PwfQgmydITADH0fBHOtKHVuMaflZ1GjQ2YR2zk9kXU9pzm0JAR+eZoISp\n" +
                "I7u21wttXaS8ljYzmk1aE0VTGrTmwGOkwsFbyjh0gFCaKUEcsd5x10OJay8WkO3D\n" +
                "ThzjmwECgYEA9hHGKI8Ki2/CJ6YgicnWl11MJE3AHmA8SRb6gwoo85kk2C7NNc6V\n" +
                "FumPLQzDrfooegKqIh7ZXrHH+BBuum9WA0xgJ6PdPN8ijkmDlRQTGQY2u+UQjqbl\n" +
                "nAEc6qt2jPVImKgtJv24BpKiVLbzcIOh9pojdpupgZRTC7KqASKmax8CgYEA6W1/\n" +
                "vVfj1nmS5Et4GVS/pNnah34v9qhYsEutToAsRsVh5LHM2thM873jmKsf34TEyQjf\n" +
                "DOMmqtOoguuNfvt9uXAQiw6MG06emoCwrF0DFwReWvDRo7oKYC7nHvP+wpwjNJps\n" +
                "sfTeyGhn/moleTkrA1MsHZ51W3zJoD5l7ORobYECgYBiWnpc7cemMhZlqKYqzO0j\n" +
                "9RuhCx62RXrzL+cTh3UU4Op0KkjFr/uxe8tJk2eHW4zgL753AuOZUO//u/m2NRgp\n" +
                "G1b3oSylzv5N5x5b8PktMGmiMo2qpxTxhRRczAoHj2bj9ZyOkC0FG4kc2T92gnaz\n" +
                "TarNFqzq6TFEZLZE2+euuwKBgFpAbwJLSBk1ujiwgUfx/4MChD6c6HiK0HehWume\n" +
                "HwBh8p6UrCr1NJONGMF/cHxgfiFoX61A6kNkQKQV5QhyvkKDdLH/Nuab+DCujyCl\n" +
                "ebtdggnRUIzoMnjLyaqrFhYyfl4pUttfXP/JdmPksIZ1Nw87CjSNfryXu/FMDuaP\n" +
                "zVyBAoGAdAAMJnckUTdpI5CMlLfUmONv/f2HmG+kIngWP/GaozmasBW4ceh1XYdy\n" +
                "WMx8pIBLFiNBRE5JiA+5EG8aujtMF1JjXnvLeYY89UamGGiQgRDUEw4B7HvCKcxC\n" +
                "+UAo/qwntk76DY6FdxVumVhpEWVMN/uRzKDWyZhtURZDu6uaCYg=";
    }
}
