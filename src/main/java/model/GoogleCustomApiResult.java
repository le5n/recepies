package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Elena_Georgievskaia
 * @since 18-Oct-17.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleCustomApiResult {
    private String kind;
    private String title;
    private String htmlTitle;
    private String link;
    private String displayLink;
    private String snippet;
    private String htmlSnippet;
    private String cacheId;
    private String formattedUrl;
    private String htmlFormattedUrl;
    private LinkedHashMap pagemap;
}
