package org.acc.word2vec.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public final class RegexUtils {

    private static final ConcurrentMap<String, Pattern> CACHE = new ConcurrentHashMap<String, Pattern>();

    private RegexUtils() {

    }

    private static Pattern getPattern(String regex) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(regex), "invalid regex");
        Pattern pattern = CACHE.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            CACHE.putIfAbsent(regex, pattern);
        }
        return pattern;
    }

    public static List<String> group(CharSequence cs, String regex) {
        if (cs == null || cs.length() == 0)
            return Collections.emptyList();
        Pattern pattern = getPattern(regex);
        Matcher matcher = pattern.matcher(cs);
        List<String> list = new ArrayList<String>();
        while (matcher.find())
            list.add(matcher.group());
        return list;
    }

    public static List<String> split(CharSequence cs, String regex) {
        if (cs == null || cs.length() == 0)
            return Collections.emptyList();
        Pattern pattern = getPattern(regex);
        return Arrays.asList(pattern.split(cs));
    }

    public static boolean match(CharSequence cs, String regex) {
        if (cs == null)
            return false;
        Pattern pattern = getPattern(regex);
        Matcher matcher = pattern.matcher(cs);
        return matcher.matches();
    }
}
