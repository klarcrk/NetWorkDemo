package network.tutoria.com.networkdemo.bean;

import java.util.List;

/**
 * Created on 2017/9/30 18:20.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class GanHuoResult {
    private boolean error;
    private List<GanHuo> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<GanHuo> getResults() {
        return results;
    }

    public void setResults(List<GanHuo> results) {
        this.results = results;
    }
}
