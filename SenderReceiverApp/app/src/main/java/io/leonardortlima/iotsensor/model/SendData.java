package io.leonardortlima.iotsensor.model;

public class SendData {

    private String timestamp;

    private String value;

    public SendData() {
    }

    public SendData(String timestamp, String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
