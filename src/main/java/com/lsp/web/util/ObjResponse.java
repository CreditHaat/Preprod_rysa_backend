package com.lsp.web.util;

public class ObjResponse<T> {
    private int code;
    private String msg;
    private T obj;

    public static<T> ObjResponse<T> success(T obj){
        ObjResponse<T> resp = new ObjResponse<>();
        resp.setCode(1);
        resp.setMsg("success");
        resp.setObj(obj);
        return resp;
    }

    public static<T> ObjResponse<T> success(T obj,String msg){
        ObjResponse<T> resp = new ObjResponse<>();
        resp.setCode(1);
        resp.setMsg(msg);
        resp.setObj(obj);
        return resp;
    }

    public static<T> ObjResponse<T> fail(String msg){
        ObjResponse<T> resp = new ObjResponse<>();
        resp.setCode(-1);
        resp.setMsg(msg);
        resp.setObj(null);
        return resp;
    }

    public static<T> ObjResponse<T> fail(T obj,String msg){
        ObjResponse<T> resp = new ObjResponse<>();
        resp.setCode(-1);
        resp.setMsg(msg);
        resp.setObj(obj);
        return resp;
    }

    public static<T> ObjResponse<T> exception(String msg){
        ObjResponse<T> resp = new ObjResponse<>();
        resp.setCode(-2);
        resp.setMsg(msg);
        resp.setObj(null);
        return resp;
    }

    public static<T> ObjResponse<T> exception(T obj,String msg){
        ObjResponse<T> resp = new ObjResponse<>();
        resp.setCode(-2);
        resp.setMsg(msg);
        resp.setObj(obj);
        return resp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
