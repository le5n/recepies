package model;

/**
 * @author Elena_Georgievskaia
 * @since 12-Nov-17.
 */
public enum SitesEnum {
    EDA_RU("https://eda.ru/recepty/vegetarianskaya-eda"),
    VEGETARIAN_RU("https://vegetarian.ru/recipes/");

    String url;

    SitesEnum(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
