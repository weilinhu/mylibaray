package com.zhanle.upgrade;

public class Error {
    public static int NO_SDCARD = -1;
    public static int EXCEPTION = -4;
    public static int FAILURE = -1;
    public static int INVALID_INSTALL = -6;
    public static int INVALID_INTENT = -7;
    private int a;
    private String b;

    public Error(int i, String str) {
        this.a = i;
        this.b = str;
    }

    public int getErrorCode() {
        return this.a;
    }

    public String getErrorMsg() {
        return this.b;
    }

    public void setErrorCode(int i) {
        this.a = i;
    }

    public void setErrorMsg(String str) {
        this.b = str;
    }

    public String toString() {
        return "Error : code=" + getErrorCode() + ",msg=" + getErrorMsg();
    }
}
