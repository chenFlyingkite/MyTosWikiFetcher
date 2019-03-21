package main.fetcher.web;

public interface OnWebFetch {
    default void onPreExecute(String link) {}
    default void onPostExecute() {}
    default void onDestroy() {}
}
