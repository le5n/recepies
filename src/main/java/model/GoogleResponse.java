package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GoogleResponse {
   private String kind;
   private LinkedHashMap url;
   private LinkedHashMap queries;
   private LinkedHashMap context;
   private LinkedHashMap searchInformation;
   private List<GoogleCustomApiResult> items;


}
