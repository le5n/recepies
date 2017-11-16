import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.GoogleCustomApiResult;
import model.GoogleResponse;
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

import static model.SitesEnum.EDA_RU;
import static model.SitesEnum.VEGETARIAN_RU;

/**
 * @author Elena_Georgievskaia
 * @since 19-Oct-17.
 */
public class ReceiptSearcher {
    private final String USER_AGENT = "Mozilla/5.0";

    public String getRandomReceipt(String keyWord) throws IOException, ParserException {
        HttpURLConnection con = sendGet("https://www.googleapis.com/customsearch/v1?key=AIzaSyDmuS51uSVl5AB1G6xYbooTIyPQESRyi-U&cx=000128958346218832896:x0xru7tbm8o&q=\"" + keyWord + "\"");

        List<GoogleCustomApiResult> pages = mapResults(con);
        String result;

        if (pages.size() < 1) {
            return "Не удалось ничего найти";
        }

        Random random = new Random();
        String link = pages.get(random.nextInt(pages.size())).getLink();

        if (link.contains(VEGETARIAN_RU.getUrl())) {
            result = link;
        } else if (link.contains(EDA_RU.getUrl())) {
            Map<String, String> pageLinksMap = getPageLinksMap(link);
            List<String> validResults = filterLinksByKeyWord(keyWord, pageLinksMap.keySet());
            result = pageLinksMap.get(validResults.get(random.nextInt(validResults.size())));

        } else {
            result = "Не удалось ничего найти";
        }
        return result;
    }

    private HttpURLConnection sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        return con;
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

    private List<String> filterLinksByKeyWord(String word, Set<String> keys) {
        return keys.stream().filter(k -> k.contains(word.substring(0, word.length() - 3))).collect(Collectors.toList());
    }
}
