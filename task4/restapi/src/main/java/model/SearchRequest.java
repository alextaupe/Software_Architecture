package model;

import java.io.Serializable;

public class SearchRequest implements Serializable {
    private final String category;
    private final String name;
    private final Poi poi;
    private final Integer distance;

    public static class Builder {
        private String category;
        private String name;
        private Poi poi;
        private Integer distance;

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder poi(Poi poi) {
            this.poi = poi;
            return this;
        }

        public Builder distance(Integer distance) {
            this.distance = distance;
          return this;
        }

        public SearchRequest build() {
            if (poi != null && distance == null) {
                throw new IllegalStateException("Distance must be set if Poi is set.");
            }
            if (distance != null && distance <= 0) {
                throw new IllegalStateException("Distance must be positive.");
            }
            return new SearchRequest(this);
        }
    }

    public boolean isEmpty() {
        if ( ( category != null && !category.isEmpty() )
          || ( name != null && !name.isEmpty() )
          || ( poi != null /* && !poi.isEmpty() */ )
          || ( distance != null && distance != 0) ) {
            return false;
        }
        return true;
    }

    /**
     * Do NOT use constructor;
     * use builder instead!
     * @param builder handed to constructor from builder
     */
    private SearchRequest(Builder builder) {
        this.category = builder.category;
        this.name = builder.name;
        this.poi = builder.poi;
        this.distance = builder.distance;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Poi getPoi() {
        return poi;
    }

    public Integer getDistance() {
        return distance;
    }
}
