package com.project.demo.logic.http;

import org.springframework.http.HttpEntity;
import org.springframework.lang.Nullable;

public class HttpResponse<T>{
    private String message;
    private T data;
    private Meta meta;

    public HttpResponse(String message) {
        this.message = message;
    }

    public HttpResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public HttpResponse(String message, T data, Meta meta) {
        this.message = message;
        this.data = data;
        this.meta = meta;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
