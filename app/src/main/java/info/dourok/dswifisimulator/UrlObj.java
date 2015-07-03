package info.dourok.dswifisimulator;

/**
 * Created by charry on 2015/7/3.
 */
public class UrlObj {
    public String url;
    public int weight;

    public UrlObj(String url) {
        this(url, 1);
    }

    public UrlObj(String url, int weight) {
        this.url = url;
        this.weight = weight;
    }
}
