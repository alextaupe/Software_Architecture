package model;

import java.io.Serializable;
import java.util.List;

public class CategoryResult implements Serializable {
    private final boolean statusOk;
    private final String statusMessage;
    private final List<String> result;

    public static class Builder {
        private boolean statusOk = true;
        private String statusMessage;
        private List<String> result;

        public Builder error(String statusMessage) {
            this.statusOk = false;
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder result(List<String> result) {
            this.result = result;
            return this;
        }

        public CategoryResult build() {
            // TODO: validate
            return new CategoryResult(this);
        }
    }

    private CategoryResult(Builder builder) {
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

    public List<String> getResult() {
        return result;
    }
}
