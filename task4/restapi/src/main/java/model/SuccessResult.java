package model;

import java.io.Serializable;

public class SuccessResult implements Serializable {
    private final boolean statusOk;
    private final String statusMessage;

    public static class Builder {
        private boolean statusOk = true;
        private String statusMessage;

        public Builder error(String statusMessage) {
            this.statusOk = false;
            this.statusMessage = statusMessage;
            return this;
        }

        public SuccessResult build() {
            // TODO: validate
            return new SuccessResult(this);
        }
    }

    private SuccessResult(Builder builder) {
        this.statusOk = builder.statusOk;
        this.statusMessage = builder.statusMessage;
    }

    public boolean isStatusOk() {
        return statusOk;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
