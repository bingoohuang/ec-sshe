package org.n3r.sshe.collector;

import org.apache.commons.lang3.StringUtils;

public class ContainsMatcher implements CollectorMatcher {
    private final String substr;
    private final boolean not;

    public ContainsMatcher(String substr, boolean not) {
        this.substr = substr;
        this.not = not;
    }

    @Override
    public boolean match(String response) {
        boolean contains = StringUtils.containsIgnoreCase(response, substr);
        return not ? !contains : contains;
    }
}
