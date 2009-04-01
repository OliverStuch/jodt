package org.jodt.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternListFilter extends AbstractFilter<String> {

    private Pattern pattern;

    public PatternListFilter(String patternAsString) {
        pattern = Pattern.compile(patternAsString);
    }

    public boolean exclude(String t) {
        return false;
    }

    public boolean include(String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

}
