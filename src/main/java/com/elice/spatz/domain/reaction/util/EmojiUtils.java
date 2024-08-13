package com.elice.spatz.domain.reaction.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtils {

    // 이모지에 대한 정규식 패턴
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[\uD83C-\uDBFF\uDC00-\uDFFF]+|[\u2600-\u27BF]|[\u2190-\u21FF]");

    //문자열에서 이모지 추출
    public static List<String> extractEmojis(String input) {
        List<String> emojis = new ArrayList<>();
        Matcher matcher = EMOJI_PATTERN.matcher(input);
        while (matcher.find()) {
            emojis.add(matcher.group());
        }
        return emojis;
    }

    // 문자열에서 이모지 제거
    public static String removeEmojis(String input) {
        return EMOJI_PATTERN.matcher(input).replaceAll("");
    }
}
