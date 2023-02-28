package model;

import java.io.Serializable;
import java.util.List;

public class SearchResult implements Serializable {
    private final boolean statusOk;
    private final String statusMessage;
    private final List<Shop> result;

    public static class Builder {
        private boolean statusOk = true;
        private String statusMessage;
        private List<Shop> result;

        public Builder error(String statusMessage) {
            this.statusOk = false;
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder result(List<Shop> result) {
            this.result = result;
            return this;
        }

        public SearchResult build() {
            // TODO: validate
            return new SearchResult(this);
        }
    }

    private SearchResult(Builder builder) {
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

    public List<Shop> getResult() {
        return result;
    }
}
