package model;

import java.io.Serializable;
import java.util.List;

public class FavouriteResult implements Serializable {
    private final boolean statusOk;
    private final String statusMessage;
    private final List<Favourite> result;

    public static class Builder {
        private boolean statusOk = true;
        private String statusMessage;
        private List<Favourite> result;

        public Builder error(String statusMessage) {
            this.statusOk = false;
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder result(List<Favourite> result) {
            this.result = result;
            return this;
        }

        public FavouriteResult build() {
            // TODO: validate
            return new FavouriteResult(this);
        }
    }

    private FavouriteResult(Builder builder) {
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

    public List<Favourite> getResult() {
        return result;
    }
}
