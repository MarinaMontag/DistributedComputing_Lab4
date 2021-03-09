package model;

public class Data {
    private String FIO;
    private String mobile;

    public Data() {
    }

    public Data(String FIO, String mobile) {
        this.FIO = FIO;
        this.mobile = mobile;
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
