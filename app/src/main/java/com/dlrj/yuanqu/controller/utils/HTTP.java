package com.dlrj.yuanqu.controller.utils;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Dizner on 2017/7/28.
 * 网络请求类
 */

public class HTTP {

    private static Retrofit retrofit;
    private static RequestManager manager;
    private static HTTP instance;
    private static boolean isDeBug = true; //是否是调试模式
    private static String TAG = "HTTP";
    private static boolean isInit = false; //是否已经初始化
    public static final int SUCCESS = 1; // 请求成功
    public static final int FAILURE = 0; // 请求失败
    private Context context;
    private static boolean isAscending = true; //校验时参数排序是否是升序
    private OkHttpClient okClient = null;

    private HTTP() {
    }


    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            if (isDeBug)
                Log.i(TAG, "请求过程 ==== " + message);
        }
    }).setLevel(HttpLoggingInterceptor.Level.BODY);

    /**
     * 初始化请求器
     *
     * @param baseUrl Base路径
     * @return
     */
    public HTTP init(final Context context, String baseUrl) {
        this.context = context;
        okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("content-type", "form-data;charset=UTF-8") //数据格式
                                .addHeader("Content-Disposition", "form-data") //数据格式
                                .addHeader("Token", "ABC_TEST")
                                .addHeader("DeviceID", SP.DRIVE_IMEI(context))
                                .build();
                        return chain.proceed(request);
                    }
                })
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request request = chain.request();
//                        okhttp3.Response proceed = chain.proceed(request);
//                        if (isDeBug) {
//                            Log.i(TAG, "requestMethod==== " + request.method());
//                            Log.i(TAG, "requestURL==== " + request.url());
//                            Log.i(TAG, "requestHeaders==== " + proceed.headers().toString());
//                        }
//
//
//                        return proceed;
//                    }
//                })
                .addInterceptor(httpLoggingInterceptor)
                .build();

/*
        //设置取消SSL证书验证，Https请求用
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        HostnameVerifier hv1 = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        String workerClassName = "okhttp3.OkHttpClient";
        try {
            Class workerClass = Class.forName(workerClassName);
            java.lang.reflect.Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(okClient, hv1);

            java.lang.reflect.Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            sslSocketFactory.set(okClient, sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

*/


        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okClient)
                .build();

        manager = retrofit.create(RequestManager.class);
        isInit = true;
        return this;
    }

    //单例-获取实例
    public static HTTP getInstance() {
        if (instance == null) {
            instance = new HTTP();
        }
        return instance;
    }

    //设置校验时参数排序方式
    public HTTP setIsAscending(boolean isAscending) {
        this.isAscending = isAscending;
        return this;
    }

    /**
     * 设置Log Tag
     *
     * @param TAG
     * @return
     */
    public HTTP setTAG(String TAG) {
        HTTP.TAG = TAG;
        return this;
    }

    /**
     * 是否开启Log（调试模式）
     *
     * @param isDeBug
     * @return
     */
    public HTTP setIsDeBug(boolean isDeBug) {
        HTTP.isDeBug = isDeBug;
        return this;
    }

    /**
     * 请求网络 From_Data+Post并进行参数加密的请求方式
     *
     * @param app_id          App_ID
     * @param requestDataBean 请求参数实体
     * @param apiUrl          请求路径
     * @param callBack        请求回调监听
     */
    public void postFromData(String app_id, Object requestDataBean, String apiUrl, final HttpRequestCallBack callBack) {
        if (!isInit || okClient == null) {
            try {
                throw new Exception("网络请求工具未进行初始化，请在Application类中进行初始化。");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (TextUtils.isEmpty(AES_KEY) || AES_KEY.length() != 16) {
            try {
                throw new Exception("AES秘钥格式异常，长度为16位。");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String requestData = getRequestData(requestDataBean);
            if (isDeBug)
                Log.d(TAG, requestData);
            manager.Request(app_id, requestData, apiUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            callBack.onStart();
                        }
                    }).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    callBack.onFinished();
                    if (isDeBug)
                        Log.d(TAG, "请求结束");
                }
            }).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(ResponseBody s) {
                    try {
                        String json = s.string();
                        JSONObject jsonObject = new JSONObject(json);
                        String data = jsonObject.optString("data");
                        if (isDeBug)
                            Log.d(TAG, "返回数据：" + data);
                        if (jsonObject.optString("code").equals("0000")) {
                            // TODO: 2017/7/10 请求响应成功
                            if (getResponseData(jsonObject.optString("data"))) {
                                // TODO: 2017/7/10 签名验证成功
                                callBack.onSuccessful(data);
                            } else {
                                // TODO: 2017/7/10 签名校验异常
                                Toast.makeText(context, "签名校验异常", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // TODO: 2017/7/10 请求响应异常
                            Toast.makeText(context, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        try {
                            PrintWriter printWriter = new PrintWriter("JSON数据格式异常");
                            e.printStackTrace(printWriter);
                            printWriter.close();
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }, new Action1<Throwable>() {
                // TODO: 2017/7/7 请求出错
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    callBack.onError(throwable);
                    if (isDeBug)
                        Log.d(TAG, "请求出错：" + throwable.toString());
                }
            });
        }
    }


    /**
     * 请求网络 Post+Json明文请求方式
     *
     * @param requestDataBean 请求参数实体
     * @param apiUrl          请求路径
     * @param callBack        请求回调监听
     */
    public void postJsonData(Object requestDataBean, String apiUrl, final HttpRequestCallBack callBack) {
        if (!isInit || okClient == null) {
            try {
                throw new Exception("网络请求工具未进行初始化，请在Application类中进行初始化。");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String requestData = gson.toJson(requestDataBean);
            if (isDeBug)
                Log.d(TAG, requestData);

            manager.PostRequest(apiUrl, requestData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            callBack.onStart();
                        }
                    }).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    callBack.onFinished();
                    if (isDeBug)
                        Log.d(TAG, "请求结束");
                }
            }).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(ResponseBody s) {
                    try {
                        String json = s.string();
                        callBack.onSuccessful(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new Action1<Throwable>() {
                // TODO: 2017/7/7 请求出错
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    callBack.onError(throwable);
                    if (isDeBug) {
                        Log.d(TAG, "请求出错：" + throwable.toString());
                    }
                }
            });
        }
    }


    /**
     * 请求网络 Post+Json明文请求方式
     *
     * @param requestDataBean 请求参数实体
     * @param apiUrl          请求路径
     * @param callBack        请求回调监听
     */
    public void getJsonData(Object requestDataBean, String apiUrl, final HttpRequestCallBack callBack) {
        if (!isInit || okClient == null) {
            try {
                throw new Exception("网络请求工具未进行初始化，请在Application类中进行初始化。");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }  else {
            Map<String, String> map2 = new TreeMap<>(comparator) ;
            if (requestDataBean !=null) {
                String requestData = gson.toJson(requestDataBean);
//            String requestData = "{'name':'测试','psw':'123456'}";
                map2 = json2Map(requestData);
//            Map<String, String> map2 = new TreeMap<>();
//            map2.put("name","测试");
//            map2.put("psw","123456");
                if (isDeBug)
                    Log.d(TAG, requestData);
            }


            manager.GetRequest(apiUrl, map2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (isDeBug)
                                Log.d(TAG, "请求开始");
                            callBack.onStart();
                        }
                    }).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    callBack.onFinished();
                    if (isDeBug)
                        Log.d(TAG, "请求结束");
                }
            }).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(ResponseBody s) {
                    try {
                        String json = s.string();
                        callBack.onSuccessful(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new Action1<Throwable>() {
                // TODO: 2017/7/7 请求出错
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    callBack.onError(throwable);
                    if (isDeBug)
                        Log.d(TAG, "请求出错：" + throwable.toString());
                }
            });
        }
    }


    /**
     * 文件下载
     *
     * @param apiUrl   请求路径
     * @param callBack 请求回调监听
     */
    String fileName = null;

    public void getFileData(String apiUrl, final HttpDownLoadCallBack callBack) {
        downLoadcallBack = callBack;
        if (!TextUtils.isEmpty(apiUrl)) {
            fileName = apiUrl.substring(apiUrl.lastIndexOf("/") + 1, apiUrl.length());
            Log.d("RRTT", fileName);
        }

        if (!isInit || okClient == null) {
            try {
                throw new Exception("网络请求工具未进行初始化，请在Application类中进行初始化。");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//

            manager.GetFileStream(apiUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mHandler.sendEmptyMessage(5);
                        }
                    }).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    mHandler.sendEmptyMessage(4);
//                    callBack.onFinished();
                    if (isDeBug)
                        Log.d(TAG, "请求结束");
                }
            }).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(ResponseBody response) {
                    saveFileByResponseBody(response, fileName);
                }
            }, new Action1<Throwable>() {
                // TODO: 2017/7/7 请求出错
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    downLoadthrowable = throwable;
                    mHandler.sendEmptyMessage(3);
                    if (isDeBug)
                        Log.d(TAG, "请求出错：" + throwable.toString());
                }
            });
        }
    }


    /**
     * 文件上传--未进行测试
     *
     * @param apiUrl   请求路径
     * @param callBack 请求回调监听
     */
    public void putFileData(String apiUrl, String filePath, final ProgressRequestListener callBack) {
        if (!isInit || okClient == null) {
            try {
                throw new Exception("网络请求工具未进行初始化，请在Application类中进行初始化。");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            upLoadcallBack = callBack;
            File file = new File(filePath);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=" + file.getName()), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("file", file.getName(), RequestBody.create(null, file))
                    .build();
            RequestBody res = new ProgressRequestBody(requestBody, callBack);
            manager.PutFileStream(apiUrl, res)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
//                            callBack.onUpLoadStart();
                            mHandler.sendEmptyMessage(6);
                        }
                    }).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
//                    callBack.onFinished();
                    mHandler.sendEmptyMessage(9);
                    if (isDeBug)
                        Log.d(TAG, "请求结束");
                }
            }).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(ResponseBody response) {
//                    saveFileByResponseBody(response, callBack);
                    mHandler.sendEmptyMessage(8);
                }
            }, new Action1<Throwable>() {
                // TODO: 2017/7/7 请求出错
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    upLoadthrowable = throwable;
                    mHandler.sendEmptyMessage(10);
//                    callBack.onUpLoadError(throwable);
                    if (isDeBug)
                        Log.d(TAG, "请求出错：" + throwable.toString());
                }
            });
        }
    }

    //文件下载回调
    HttpDownLoadCallBack downLoadcallBack;
    //文件上传回调
    ProgressRequestListener upLoadcallBack;
    //文件下载异常
    Throwable downLoadthrowable;
    //文件上传异常
    Throwable upLoadthrowable;
    /**
     * 文件下载和上传处理Handler（文件操作放在I/O线程，回调放在UI线程处理）
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //下载
                //下载文件更新UI
                case 1:
                    FilePro obj = (FilePro) msg.obj;
                    downLoadcallBack.onDownLoading(obj.getLen(), obj.getContentLen());
                    break;
                //下载文件完成
                case 2:
                    downLoadcallBack.onDownLoadSuccessful();
                    break;
                //下载异常
                case 3:
                    downLoadcallBack.onDownLoadError(downLoadthrowable);
                    break;
                //请求结束
                case 4:
                    downLoadcallBack.onFinished();
                    break;
                //下载开始
                case 5:
                    downLoadcallBack.onDownLoadStart();
                    break;
                //上传
                //上传开始
                case 6:
                    upLoadcallBack.onUpLoadStart();
                    break;
                //上传更新UI
                case 7:
                    FilePro obj1 = (FilePro) msg.obj;
                    upLoadcallBack.onUpLoadProgress(obj1.getLen(), obj1.getContentLen(), obj1.isOver());
                    break;
                //上传成功
                case 8:
                    upLoadcallBack.onUpLoadSuccessful();
                    break;
                //请求结束
                case 9:
                    upLoadcallBack.onFinished();
                    break;
                //上传异常
                case 10:
                    upLoadcallBack.onUpLoadError(upLoadthrowable);
                    break;
            }
        }
    };

    /**
     * 将ResponseBody里的文件流存到SD卡并更新UI
     *
     * @param response
     * @param fileName
     */
    private void saveFileByResponseBody(ResponseBody response, String fileName) {
        try {
            InputStream is = response.byteStream();
            //获取文件总长度
            long totalLength = response.contentLength();
            String path = SP.APP_FILE_DOWNLOAD_PATH(context);
            File file = new File(path, fileName);
            if (file.createNewFile()) {
                file.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len, lens = 0;
            FilePro filePro;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                //此处进行更新操作
                //len即可理解为已下载的字节数(byte)
                lens += len;
                filePro = new FilePro();
                filePro.setContentLen(totalLength);
                filePro.setLen(lens);
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = filePro;
                mHandler.sendMessage(msg);
                filePro = null;
            }
            fos.flush();
            fos.close();
            bis.close();
            is.close();
            //此处就代表更新结束
            mHandler.sendEmptyMessage(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求回调监听
     */
    public interface HttpRequestCallBack {
        //成功
        void onSuccessful(String json);

        //请求完成
        void onFinished();

        //请求异常
        void onError(Throwable throwable);

        //请求开始
        void onStart();
    }

    /**
     * 下载回调监听
     */
    public interface HttpDownLoadCallBack {
        //下载成功
        void onDownLoadSuccessful();

        //下载进度
        void onDownLoading(long len, long totalLength);

        //请求完成
        void onFinished();

        //下载异常
        void onDownLoadError(Throwable throwable);

        //下载开始
        void onDownLoadStart();
    }

    // retrofit 请求管理器
    private interface RequestManager {


        @POST
        @FormUrlEncoded
        Observable<ResponseBody> Request(@Field("appid") String appid, @Field("data") String data, @Url String url);

        @POST
        Observable<ResponseBody> PostRequest(@Url String url, @Body String arg);

        @GET
        Observable<ResponseBody> GetRequest(@Url String url, @QueryMap Map<String, String> str);

        @Streaming
        @GET
        Observable<ResponseBody> GetFileStream(@Url String fileUrl);

        @POST
        Observable<ResponseBody> PutFileStream(@Url String url, @Body RequestBody request);
    }


    /**********************************************************************************************/
    /**                                                                                          **/
    /**                                    以下是请求响应，数据校验部分                             **/
    /**                                                                                          **/
    /**********************************************************************************************/

    //校验签名秘钥
    private static String APP_KEY = "3ce0e5bbdfe2dc2c76aaf3998a1f5005";
    //签名
    private static String sign;
    //AES 秘钥
    private static String AES_KEY = "1234567812345678";

    private static JSONObject jsonObject;

    //初始向量（AES加密）
    private static String IV_STRING = "0000000000000000";
    private static final String charset = "UTF-8";
    private static Map<String, String> map;


    public static String getAppKey() {
        return APP_KEY;
    }

    public HTTP setAppKey(String appKey) {
        APP_KEY = appKey;
        return this;
    }

    public static String getAesKey() {
        return AES_KEY;
    }

    public HTTP setAesKey(String aesKey) {
        AES_KEY = aesKey;
        return this;
    }

    public static String getIvString() {
        return IV_STRING;
    }

    public HTTP setIvString(String ivString) {
        IV_STRING = ivString;
        return this;
    }

    /**
     * 加密方法
     *
     * @param content 明文
     * @param key     密码
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private String aesEncryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] contentBytes = content.getBytes(charset);
        byte[] keyBytes = key.getBytes(charset);
        byte[] encryptedBytes = aesEncryptBytes(contentBytes, keyBytes);
        return Base64.encodeToString(encryptedBytes, 1);
    }

    /**
     * 解密
     *
     * @param content 密文
     * @param key     密码
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private String aesDecryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] encryptedBytes = Base64.decode(content, 1);
        byte[] keyBytes = key.getBytes(charset);
        byte[] decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes);
        return new String(decryptedBytes, charset);
    }

    private byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE);
    }

    private byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE);
    }

    /**
     * 根据密码生产秘钥
     *
     * @param contentBytes
     * @param keyBytes
     * @param mode
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] initParam = IV_STRING.getBytes(charset);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, ivParameterSpec);

        return cipher.doFinal(contentBytes);
    }


    private Map<String, String> dealargments(Object o) {
        map = new TreeMap<>(comparator);
        java.lang.reflect.Field[] fields = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {

            if (fields[i].getType().isMemberClass() || fields[i].getType().isAssignableFrom(List.class) || fields[i].getType().isAssignableFrom(ArrayList.class)) {
                // TODO: 2017/7/7 如果是成员类应该做的处理and如果是List应该做的处理

                String name = fields[i].getName();
                Object memberClass = getFieldValueByName(fields[i].getName(), o);
                String s = gson.toJson(memberClass);
                map.put(name, s);
            } else {
                map.put(fields[i].getName(), getFieldValueByName(fields[i].getName(), o).toString());
            }
        }
        map.put("timestamp", String.valueOf(new Date().getTime()));
        return map;
    }


    /**
     * 反射--根据属性名获取属性值
     */
    private Object getFieldValueByName(String fieldName, Object o) {
        Object value = null;
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            value = method.invoke(o, new Object[]{});
        } catch (NoSuchMethodException e) {

            try {
                String firstLetter = fieldName.substring(0, 1).toUpperCase();
                String getter = "is" + firstLetter + fieldName.substring(1);
                Method method = o.getClass().getMethod(getter, new Class[]{});
                value = method.invoke(o, new Object[]{});
            } catch (Exception er) {
                er.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    //自定义map排序器--升序
    private Comparator comparator = new Comparator<String>() {
        public int compare(String obj1, String obj2) {
            // 升序排序
            return obj1.compareTo(obj2);
        }
    };


    /**
     * 返回去空格并URLEncoder的String URL编码
     *
     * @param s
     * @return
     */
    private String deleteSpace_URLEncoder(String s) {
        String regex = "\\s*";

        String s_detaleSpace = s.replaceAll(regex, ""); // 去掉所有空格的字符串

        String s_deleteSpace_URLEncoder = "";
        try {
            s_deleteSpace_URLEncoder = java.net.URLEncoder.encode(s_detaleSpace, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            s_deleteSpace_URLEncoder = s_detaleSpace;
        }
        return s_deleteSpace_URLEncoder;
    }


    /**
     * 返回去空格并URLDncoder的String URL反编码
     *
     * @param s
     * @return
     */
    private String deleteSpace_URLDncoder(String s) {
        String regex = "\\s*";

        String s_detaleSpace = s.replaceAll(regex, ""); // 去掉所有空格的字符串

        String s_deleteSpace_URLDncoder = "";
        try {
            s_deleteSpace_URLDncoder = java.net.URLDecoder.decode(s_detaleSpace, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            s_deleteSpace_URLDncoder = s_detaleSpace;
        }
        return s_deleteSpace_URLDncoder;
    }


    /**
     * 利用MD5进行签名
     *
     * @param input 待加密的字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException     没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    //密码加密 与php加密一致
    private String md5(String input, String appkey) throws NoSuchAlgorithmException {
        input = input + appkey;
        String result = input;
        if (isDeBug)
            System.out.println("组合结果：" + result);
        if (input != null) {
            MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
            md.update(input.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while (result.length() < 32) { //31位string
                result = "0" + result;
            }
        }
        return result;
    }

    /**
     * json转map 只转一层结构，含有复合对象结构只当做字符串处理
     *
     * @param object
     * @return
     */
    private Map<String, String> json2Map(String object) {
        Map<String, String> data = new TreeMap<String, String>(comparator);
        // 将json字符串转换成jsonObject
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator ite = jsonObject.keys();
        // 遍历jsonObject数据,添加到Map对象
        while (ite.hasNext()) {
            String key = ite.next().toString();
            String value = null;
            try {
                value = jsonObject.get(key).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!key.equals("sign"))
                data.put(key, value);
        }
        return data;
    }


    private static Gson gson = new Gson();

    /**
     * 进行key-value 排序组合
     *
     * @param map
     * @return
     */
    private String zuhe(Map<String, String> map) {
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuffer buffer = new StringBuffer();
        while (iter.hasNext()) {
            String key = iter.next();
            buffer.append(key + "=" + map.get(key));
            buffer.append("&");
        }
        return buffer.toString();
    }

    /**
     * 生成加密的请求参数
     *
     * @param o
     * @return
     */
    public String getRequestData(Object o) {

        String aesEncryptString = null;
        try {
            Map<String, String> tmpMap = dealargments(o);
            String buffer = zuhe(tmpMap);
            sign = md5(buffer.toString(), APP_KEY);
            if (isDeBug)
                System.out.println("拼接排序结果：" + buffer);
            if (isDeBug)
                System.out.println("MD5签名结果：" + sign);
            map.put("sign", sign);
            String json = gson.toJson(map);
            if (isDeBug)
                System.out.println("转json结果：" + json);
            String URLEncoder = deleteSpace_URLEncoder(json);
            if (isDeBug)
                System.out.println("URLEncoder结果：" + URLEncoder);

            aesEncryptString = aesEncryptString(URLEncoder, AES_KEY);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (isDeBug)
            System.out.println("AES加密结果：" + aesEncryptString);
        return aesEncryptString;
    }

    public boolean getResponseData(String data) {
//        //接收处理
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sign1 = jsonObject.optString("sign");
        if (isDeBug)
            System.out.println("获取网络数据签名结果：" + sign1);
        String timestamp = jsonObject.optString("timestamp");
        long singTime = Long.parseLong(timestamp);
        long time = System.currentTimeMillis() / 1000;
        if (time - singTime > 1000 * 3600 * 2) {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            if (isDeBug)
                System.out.println("时间戳失效");
            return false;
        }
        //对拿到的消息进行签名
        String sign2 = null;
        try {
            sign2 = md5(zuhe(json2Map(data)), APP_KEY);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (isDeBug)
            System.out.println("验证签名结果：" + sign2);
        if (sign1 != null && !sign1.equals("") && sign1.equals(sign2)) {
            if (isDeBug)
                System.out.println("签名正确");
            return true;
        } else {
            if (isDeBug)
                System.out.println("签名错误，数据可能被修改");
            return false;
        }

    }


    /**
     * 包装的请求体，处理进度
     * User:lizhangqu(513163535@qq.com)
     * Date:2015-09-02
     * Time: 17:15
     */
    public class ProgressRequestBody extends RequestBody {
        //实际的待包装请求体
        private final RequestBody requestBody;
        //进度回调接口
        private final ProgressRequestListener progressListener;
        //包装完成的BufferedSink
        private BufferedSink bufferedSink;

        /**
         * 构造函数，赋值
         *
         * @param requestBody      待包装的请求体
         * @param progressListener 回调接口
         */
        public ProgressRequestBody(RequestBody requestBody, ProgressRequestListener progressListener) {
            this.requestBody = requestBody;
            this.progressListener = progressListener;
        }

        /**
         * 重写调用实际的响应体的contentType
         *
         * @return MediaType
         */
        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        /**
         * 重写调用实际的响应体的contentLength
         *
         * @return contentLength
         * @throws IOException 异常
         */
        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        /**
         * 重写进行写入
         *
         * @param sink BufferedSink
         * @throws IOException 异常
         */
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                //包装
                bufferedSink = Okio.buffer(sink(sink));
            }
            //写入
            requestBody.writeTo(bufferedSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();

        }

        /**
         * 写入，回调进度接口
         *
         * @param sink Sink
         * @return Sink
         */
        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                //当前写入字节数
                long bytesWritten = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength();
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount;
                    //回调
                    Message msg = Message.obtain();
                    msg.what = 7;
                    FilePro filePro = new FilePro();
                    filePro.setLen(bytesWritten);
                    filePro.setContentLen(contentLength);
                    filePro.setOver(bytesWritten == contentLength);
                    msg.obj = filePro;
                    mHandler.sendMessage(msg);
//                    progressListener.onUpLoadProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                }
            };
        }
    }

    /**
     * 请求体进度回调接口，比如用于文件上传中
     * User:lizhangqu(513163535@qq.com)
     * Date:2015-09-02
     * Time: 17:16
     */
    public interface ProgressRequestListener {
        //下载进度
        void onUpLoadProgress(long bytesWritten, long contentLength, boolean done);

        //下载成功
        void onUpLoadSuccessful();

        //请求完成
        void onFinished();

        //下载异常
        void onUpLoadError(Throwable throwable);

        //下载开始
        void onUpLoadStart();
    }


    // 上传下载文件大小包装类
    public class FilePro {
        long len;
        long contentLen;
        boolean isOver;

        public boolean isOver() {
            return isOver;
        }

        public FilePro setOver(boolean over) {
            isOver = over;
            return this;
        }

        public long getLen() {
            return len;
        }

        public FilePro setLen(long len) {
            this.len = len;
            return this;
        }

        public long getContentLen() {
            return contentLen;
        }

        public FilePro setContentLen(long contentLen) {
            this.contentLen = contentLen;
            return this;
        }
    }


}
