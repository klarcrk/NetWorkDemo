package network.tutoria.com.networkdemo.other.okhttp;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created on 2017/9/30 10:59.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class UploadPostBody extends RequestBody {

    private final MediaType mediaType;
    private final File uploadFile;
    private ProgressChangedListener listener;

    public UploadPostBody(MediaType mediaType, File uploadFile) {
        this.mediaType = mediaType;
        this.uploadFile = uploadFile;
    }

    public void setListener(ProgressChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public long contentLength() throws IOException {
        return uploadFile.length();
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(uploadFile);
            //sink.writeAll(source);
            Buffer buffer = new Buffer();
            long remaining = contentLength();
            for (long readCount; (readCount = source.read(buffer, 2048)) != -1; ) {
                sink.write(buffer, readCount);
                Log.d("tag", "source size: " + contentLength() + " remaining bytes: " + (remaining -= readCount));
                if (listener != null) {
                    listener.onProgressChanged(contentLength(), remaining, contentLength() - remaining, readCount);
                }
            }
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
