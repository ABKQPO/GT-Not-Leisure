package com.science.gtnl.utils.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectorParser {

    public char selector;

    public Map<String, List<String>> args = new HashMap<>();

    public SelectorParser(String token) {

        selector = token.charAt(1);

        if (!token.contains("[")) {
            return;
        }

        String argString = token.substring(token.indexOf('[') + 1, token.length() - 1);

        String[] split = argString.split(",");

        for (String s : split) {

            String[] kv = s.split("=");

            if (kv.length == 2) {

                args.computeIfAbsent(kv[0], k -> new ArrayList<>())
                    .add(kv[1]);
            }
        }
    }

    public String get(String key) {

        List<String> list = args.get(key);

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public List<String> getAll(String key) {

        return args.getOrDefault(key, Collections.emptyList());
    }
}
