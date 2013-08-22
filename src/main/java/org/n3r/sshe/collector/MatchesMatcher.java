package org.n3r.sshe.collector;

import java.util.regex.Pattern;

public class MatchesMatcher implements CollectorMatcher {
    private final Pattern pattern;
    private final boolean not;

    public MatchesMatcher(String parameter, boolean not) {
        this.pattern = Pattern.compile(parameter);
        this.not = not;
    }

    @Override
    public boolean match(String response) {
        boolean found = pattern.matcher(response).find();
        return not ? !found : found;
    }
}
