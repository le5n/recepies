import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.GoogleCustomApiResult;
import model.GoogleResponse;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Elena_Georgievskaia
 * @since 19-Oct-17.
 */
public class ReceiptSearcher {
    private final String USER_AGENT = "Mozilla/5.0";

    public String getRandomReceipt(String keyWord) throws IOException, ParserException {
        HttpURLConnection con = sendGet("https://www.googleapis.com/customsearch/v1?key=AIzaSyDmuS51uSVl5AB1G6xYbooTIyPQESRyi-U&cx=000128958346218832896:x0xru7tbm8o&q=" + keyWord);

        List<String> linksOnPage = getLinksOnPage(mapResults(con), keyWord);
        if (linksOnPage.size() < 1) {
            return "Не удалось ничего найти";
        }
        int randomInt = new Random().nextInt(linksOnPage.size());
        return linksOnPage.get(randomInt);
    }

    private HttpURLConnection sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        return con;
    }

    private List<String> getLinksOnPage(List<GoogleCustomApiResult> pages, String keyWord) throws ParserException {
        List<String> recepiesLinks = new ArrayList<>();

        for (GoogleCustomApiResult item : pages) {
            String link = item.getLink();
            Map<String, String> linksOnPage = getPageLinksMap(link);
            Set<String> set = filterLinksByKeyWord(keyWord, linksOnPage.keySet());

            for (String elem : set) {
                String url = linksOnPage.get(elem);
                String[] splitUrl = url.split("/");
                String tail = splitUrl[splitUrl.length - 1];
                if (tail.length() > 5) {
                    String[] tailNumber = tail.split("-");
                    String targetPart = tailNumber[tailNumber.length - 1];
                    if (StringUtils.isNumeric(targetPart))
                        recepiesLinks.add(url);
                }
            }
        }
        return recepiesLinks;
    }

    private Map<String, String> getPageLinksMap(final String url) {
        Parser htmlParser = null;
        try {
            htmlParser = new Parser(url);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        final Map<String, String> result = new HashMap<>();

        try {
            if (htmlParser != null) {
                final NodeList tagNodeList = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
                for (int j = 0; j < tagNodeList.size(); j++) {
                    final LinkTag loopLink = (LinkTag) tagNodeList.elementAt(j);
                    String linkText = loopLink.getLinkText();
                    final String loopLinkStr = loopLink.getLink();
                    result.put(linkText, loopLinkStr);
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<GoogleCustomApiResult> mapResults(HttpURLConnection con) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(con.getInputStream());
        GoogleResponse jsonMap = mapper.readValue(jsonParser, GoogleResponse.class);
        return jsonMap.getItems();
    }

    private Set<String> filterLinksByKeyWord(String word, Set<String> keys) {
        return keys.stream().filter(k -> k.contains(word.substring(0, word.length() - 3))).collect(Collectors.toSet());
    }
}
