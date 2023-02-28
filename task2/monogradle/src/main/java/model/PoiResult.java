package model;

import java.io.Serializable;
import java.util.List;

public class PoiResult implements Serializable {
    private final boolean statusOk;
    private final String statusMessage;
    private final List<Poi> result;

    public static class Builder {
        private boolean statusOk = true;
        private String statusMessage;
        private List<Poi> result;

        public Builder error(String statusMessage) {
            this.statusOk = false;
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder result(List<Poi> result) {
            this.result = result;
            return this;
        }

        public PoiResult build() {
            // TODO: validate
            return new PoiResult(this);
        }
    }

    private PoiResult(Builder builder) {
        this.statusOk = builder.statusOk;
        this.statusMessage = builder.statusMessage;
        this.result = builder.result;
    }

    public boolean isStatusOk() {
        return statusOk;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public List<Poi> getResult() {
        return result;
    }
}
