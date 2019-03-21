package main.fetcher.web;

import flyingkite.log.LF;
import flyingkite.tool.TicTac2;

public class OnWebLfTT implements OnWebFetch {
    public LF mLf;
    public TicTac2 clock;
    private boolean deleteAtPre = true;

    public OnWebLfTT(LF lf, TicTac2 tt) {
        mLf = lf;
        clock = tt;
    }

    public void deleteAtPre(boolean delete) {
        deleteAtPre = delete;
    }

    @Override
    public void onPreExecute(String link) {
        if (deleteAtPre) {
            mLf.getFile().delete().open();
        } else {
            mLf.getFile().open();
        }
        mLf.log("Linking %s", link);
        clock.tic();
    }

    @Override
    public void onPostExecute() {
        clock.tac("request sent");
    }

    @Override
    public void onDestroy() {
        mLf.getFile().close();
    }
}
