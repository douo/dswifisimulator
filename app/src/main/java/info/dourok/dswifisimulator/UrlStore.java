package info.dourok.dswifisimulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Created by charry on 2015/7/3.
 */
public class UrlStore {
    private ArrayList<UrlObj> urlList;
    private Collection<UrlObj> urlStore;
    private Random mRandom;

    public UrlStore() {
        mRandom = new Random();
    }

    public void addUrl(UrlObj obj) {
        urlStore.add(obj);
        int w = obj.weight;
        while (w-- > 0) {  // memory is cheap
            urlList.add(obj);
        }
    }

    public int getSize() {
        return urlStore.size();
    }

    public String nextUrl() {
        return urlList.get((int) (mRandom.nextFloat() * urlList.size())).url;
    }
}
