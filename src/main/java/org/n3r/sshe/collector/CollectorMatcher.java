package org.n3r.sshe.collector;

public interface CollectorMatcher {
    boolean match(String response);
}
