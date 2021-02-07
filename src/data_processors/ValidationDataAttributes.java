package data_processors;

import java.util.Objects;

public class ValidationDataAttributes {

    public class QueryAttributes {

        private String queryId;
        private String query;

        public QueryAttributes(String queryId, String query) {
            this.queryId = queryId;
            this.query = query;
        }

        public String getQueryId() {
            return queryId;
        }

        public void setQueryId(String queryId) {
            this.queryId = queryId;
        }

        public String getQuery() {
            return query;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QueryAttributes that = (QueryAttributes) o;
            return Objects.equals(queryId, that.queryId) &&
                    Objects.equals(query, that.query);
        }

        @Override
        public int hashCode() {
            return Objects.hash(queryId, query);
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    public class PassageAttributes {

        private String passageId;
        private String passage;
        private double relevanceScore;

        public PassageAttributes(String passageId, String passage, double relevanceScore) {
            this.passageId = passageId;
            this.passage = passage;
            this.relevanceScore = relevanceScore;
        }

        public String getPassageId() {
            return passageId;
        }

        public void setPassageId(String passageId) {
            this.passageId = passageId;
        }

        public String getPassage() {
            return passage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PassageAttributes that = (PassageAttributes) o;
            return Double.compare(that.relevanceScore, relevanceScore) == 0 &&
                    Objects.equals(passageId, that.passageId) &&
                    Objects.equals(passage, that.passage);
        }

        @Override
        public int hashCode() {
            return Objects.hash(passageId, passage, relevanceScore);
        }

        public void setPassage(String passage) {
            this.passage = passage;
        }

        public double getRelevanceScore() {
            return relevanceScore;
        }

        public void setRelevanceScore(double relevanceScore) {
            this.relevanceScore = relevanceScore;
        }
    }



































//    private String queryId;
//    private String passageId;
//    private String query;
//    private String passage;
//    private double relevanceScore;
//
//    public ValidationDataAttributes(String queryId, String passageId, String query, String passage, double relevanceScore) {
//        this.queryId = queryId;
//        this.passageId = passageId;
//        this.query = query;
//        this.passage = passage;
//        this.relevanceScore = relevanceScore;
//    }
//
//    public String getQueryId() {
//        return queryId;
//    }
//
//    public void setQueryId(String queryId) {
//        this.queryId = queryId;
//    }
//
//    public String getPassageId() {
//        return passageId;
//    }
//
//    public void setPassageId(String passageId) {
//        this.passageId = passageId;
//    }
//
//    public String getQuery() {
//        return query;
//    }
//
//    public void setQuery(String query) {
//        this.query = query;
//    }
//
//    public String getPassage() {
//        return passage;
//    }
//
//    public void setPassage(String passage) {
//        this.passage = passage;
//    }
//
//    public double getRelevanceScore() {
//        return relevanceScore;
//    }
//
//    public void setRelevanceScore(double relevanceScore) {
//        this.relevanceScore = relevanceScore;
//    }
}