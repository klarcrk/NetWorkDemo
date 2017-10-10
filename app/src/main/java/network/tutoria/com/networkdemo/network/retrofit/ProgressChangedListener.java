package network.tutoria.com.networkdemo.network.retrofit;

/**
 * Created on 2017/10/10 14:16.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public interface ProgressChangedListener {
    //不是主线程
    void onProgressChanged(long byteTotalCount, long remainByteCount, long readCount, long countReadThisTime);
}
