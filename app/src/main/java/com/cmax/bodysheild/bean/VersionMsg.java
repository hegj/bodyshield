package com.cmax.bodysheild.bean;

/**
 * Created by hegj on 2015/12/6.
 */
public class VersionMsg {
    private String s_code;
    private String s_message;
    private VersionResult a_result;

    public String getS_code() {
        return s_code;
    }

    public void setS_code(String s_code) {
        this.s_code = s_code;
    }

    public String getS_message() {
        return s_message;
    }

    public void setS_message(String s_message) {
        this.s_message = s_message;
    }

    public VersionResult getA_result() {
        return a_result;
    }

    public void setA_result(VersionResult a_result) {
        this.a_result = a_result;
    }
}
