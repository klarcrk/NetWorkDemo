package network.tutoria.com.networkdemo.network.retrofit;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created on 2017/9/30 15:24.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public interface RetrofitRequestService {

    @GET()
    Observable<retrofit2.Response<ResponseBody>> get(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> queryParams);

    @FormUrlEncoded
    @POST
    Observable<retrofit2.Response<ResponseBody>> post(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, String> queryParams);


    @FormUrlEncoded
    @POST
    Observable<retrofit2.Response<ResponseBody>> postWithObject(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, Object> requestPart);


    @Streaming
    @GET
    Observable<retrofit2.Response<ResponseBody>> downloadFile(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> queryParams);

    @Multipart
    @POST
    Observable<retrofit2.Response<ResponseBody>> uploadFile(@HeaderMap Map<String, String> headers, @Url String url, @Part List<MultipartBody.Part> parts);

}
