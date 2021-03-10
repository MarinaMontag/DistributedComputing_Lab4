package model;

import java.io.Serializable;

public class Data implements Serializable {
    private final String FIO;
    private final String mobile;

    public Data(String FIO, String mobile) {
        this.FIO = FIO;
        this.mobile = mobile;
    }

    public String getFIO() {
        return FIO;
    }

    public String getMobile() {
        return mobile;
    }

    @Override
    public String toString() {
        return "Data{" +
                "FIO='" + FIO + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
