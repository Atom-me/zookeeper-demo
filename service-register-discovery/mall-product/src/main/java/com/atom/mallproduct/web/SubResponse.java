package com.atom.mallproduct.web;

/**
 * @author Atom
 */
public class SubResponse<T> extends Response<T> {

    public SubResponse(String code, String msg) {
        super(code, msg);
    }

    public SubResponse(String code, String msg, T data) {
        super(code, msg, data);
    }

    /**
     * 获取失败结构体
     *
     * @param <T>
     * @return
     */
    public static <T> SubResponse<T> errorResult(String errorCode, String msg) {
        SubResponse<T> webResultResponse = new SubResponse<T>(errorCode, msg);
        return webResultResponse;
    }
}
