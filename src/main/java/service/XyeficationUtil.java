package service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Elena_Georgievskaia
 * @since 16-Nov-17.
 */
public class XyeficationUtil {
    public static String getXyeficatedString(String messageText) {
        String keyword = "";
        Pattern wordPattern = Pattern.compile("(\\S+)$");
        Matcher wordMatcher = wordPattern.matcher(messageText);
        if (wordMatcher.find()) {
            keyword = wordMatcher.group();
        }
        Integer keywordLength = keyword.length();
        String xye;
        if (keywordLength <= 3) {
            Pattern firstIsVowel = Pattern.compile("^[аеёийоуэюя]");
            Matcher matcher = firstIsVowel.matcher(keyword);
            if (matcher.find()) {
                xye = getXyefication(keyword, 0);
            } else {
                xye = getDefaultXyefication(keyword);
            }
        } else {
            Pattern includesVowel = Pattern.compile("[аеёийоуэюя]\\B");
            Matcher matcher = includesVowel.matcher(keyword);
            if (matcher.find()) {
                xye = getXyefication(keyword, matcher.start());
            } else {
                xye = getDefaultXyefication(keyword);
            }
        }
        return xye;
    }

    private static String getDefaultXyefication(String word) {
        return "хуе" + word;
    }

    private static String getXyefication(String word, Integer vowelIndex) {
        char vowel = word.charAt(vowelIndex);
        return getPrefix(vowel) + word.substring(vowelIndex + 1);
    }

    private static String getPrefix(char vowelLetter) {
        char fixedVowel;
        switch (vowelLetter) {
            case 'a':
                fixedVowel = 'я';
                break;
            case 'э':
            case 'ё':
            case 'й':
            case 'о':
                fixedVowel = 'е';
                break;
            case 'у':
                fixedVowel = 'ю';
                break;
            default:
                fixedVowel = vowelLetter;
                break;
        }

        return "ху" + fixedVowel;
    }
}
