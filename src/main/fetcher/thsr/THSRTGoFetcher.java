package main.fetcher.thsr;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.FetcherUtil;
import main.fetcher.data.thsr.THSRFare;
import main.fetcher.data.thsr.THSRTimeTable;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import main.kt.THSRGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class THSRTGoFetcher {
    // Basic pack
    public static final THSRTGoFetcher me = new THSRTGoFetcher();
    private static final String FOLDER = "thsr";
    private LF mLf = new LF(FOLDER);
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();
    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

    public static void main(String[] args) {
        me.parse();
    }

    // 星巴克 便利生活 美食餐廳 點心伴手禮 旅遊交通
    // 4 5 6 7 9
    // like
    // https://tgopoints.thsrc.com.tw/product?cat1=4

    private void parse() {
        mLf.getFile().open(false);
        clock.tic();
        //loadPriceTables();
        parse2020();
        clock.tac("THSRTGoFetcher.parse OK");
        mLf.getFile().close();
    }

    private void parse2020() {
        final String thsrRes = "thsrRes";
        File[] fs = {
              new File(thsrRes, "2022/20220104_TVMs_manual.txt")
            , new File(thsrRes, "2022/2022_MothersDay.txt")
            , new File(thsrRes, "2022/2022_LaborDay.txt")
        };
        String[] nameTable = {"2022/1/4 起適用時刻表", "母親節假期時刻表", "勞動節假期時刻表",};
        String[] startDate = {"2022/01/04", "2022/05/06", "2022/04/29",};
        String[] endDate   = {"2030/01/04", "2022/05/09", "2022/05/03",};
        for (int i = 0; i < fs.length; i++) {
            File f = fs[i];
            L.log("parse file[%s] = %s", i, f.getAbsolutePath());
            THSRTimeTable time = new THSRTimeTable();
            time.name = nameTable[i];
            time.startDate = startDate[i];
            time.endDate = endDate[i];
            time.parse(f);
            L.log("timeTable = %s", time);
            FetcherUtil.pretty = false;
            FetcherUtil.saveAsJson(time, thsrRes, i + ".txt");
            FetcherUtil.pretty = true;
        }

    }

    // like link = "https://www.thsrc.com.tw/Attachment/Download?pageID=a3b630bb-1066-4352-a1ef-58c7b4e8ef7c&id=618edce6-8247-4cfa-933e-24b1f9b27d98";
    private void peek(String link) {
        Document doc = fetcher.getDocument(link);
        L.log("doc = %s", doc);
    }

    // 20220104起_TVMs
    // https://en.thsrc.com.tw/Attachment/Download?pageID=a3b630bb-1066-4352-a1ef-58c7b4e8ef7c&id=3e502f1d-ff75-4404-bcc9-043ce1573f9e
    // 2022_母親節疏運時刻表
    // https://www.thsrc.com.tw/Attachment/Download?pageID=a3b630bb-1066-4352-a1ef-58c7b4e8ef7c&id=618edce6-8247-4cfa-933e-24b1f9b27d98

    private void loadPriceTables() {
        LF price = new LF(FOLDER, "prices.txt");
        price.getFile().open(false);
        clock.tic();
        String[] links = {
            "https://www.thsrc.com.tw/ArticleContent/743c51ac-124d-4b1a-a57b-1fd820848032"
            , "https://www.thsrc.com.tw/ArticleContent/1ee878bf-475a-40d0-8f88-44806522427c"
            , "https://www.thsrc.com.tw/ArticleContent/fe34c4a1-d274-48ae-bcbe-f8eff4c2faf3#MultiRide"
            // no for periodic
            // https://www.thsrc.com.tw/ArticleContent/fe34c4a1-d274-48ae-bcbe-f8eff4c2faf3#Periodic
            // early bird
            , "https://www.thsrc.com.tw/ArticleContent/7039d17d-1463-4c14-ad93-4d491dedcad5"
            // College
            , "https://www.thsrc.com.tw/ArticleContent/530e869c-479d-441a-a4b4-61a8166827e9"
        };
        List<List<THSRFare>> ans = new ArrayList<>();
        for (int i = 0; i < links.length; i++) {
            String li = links[i];
            List<THSRFare> pi = loadPriceTable(li);
            ans.add(pi);
        }
        GsonUtil.writePrettyJson(price.getFile().getFile(), ans);
        clock.tac("THSRTGoFetcher.loadPriceTables OK");
        price.getFile().close();
    }

    private List<THSRFare> loadPriceTable(String link) {
        Document doc = fetcher.getDocument(link);
        if (doc == null) return null;
        Elements es = doc.getElementsByClass("table_con");
        clock.tic();
        List<THSRFare> fareList = THSRGet.me.fare(es);
        clock.tac("THSRGet.me.fare() OK");
        L.log("%s Fare table", fareList.size());
        for (int i = 0; i < fareList.size(); i++) {
            L.log("#%s : %s", i, fareList.get(i));
        }
        return fareList;
    }

    private void ln(Elements es) {
        if (es == null) {
            L.log("null es");
        } else {
            int n = es.size();
            L.log("%s items", n);
            for (int i = 0; i < n; i++) {
                L.log("#%d : %s", i, es.get(i));
            }
        }
    }

    private void getCoupon() {
        // failed on its jsp dynamic server content...
        String base = "https://tgopoints.thsrc.com.tw/product?cat1=";

        for (int i = 1; i <= 11; i++) {
            String link = base + i;
            Document doc = fetcher.getDocument(link);
            L.log("#%s : %s", i, doc);
        }
    }

    // 時刻表與票價查詢
    // https://www.thsrc.com.tw/ArticleContent/a3b630bb-1066-4352-a1ef-58c7b4e8ef7c
    // 票價產品一覽表
    // https://www.thsrc.com.tw/ArticleContent/caa6fac8-b875-4ad6-b1e6-96c2902d12a6
    // 網路訂票
    // 24H零時差零距離、方便又快速
    // https://www.thsrc.com.tw/ArticleContent/dea241a9-fe69-4e9d-b9a5-6caed6e486d6


    // 優待票, 團體票, 回數票,
    // Concession, Group, MultiRide

// view-source:https://www.thsrc.com.tw/ArticleContent/a3b630bb-1066-4352-a1ef-58c7b4e8ef7c#scheduleDownload
//    search: function () {
//        $('#sePriceTable').hide();
//        var discountType = $('#offer').val().join(',');
//
//        $.ajax({
//                url: '/TimeTable/Search',
//                type:'POST',
//                data: {
//            SearchType: $('#typesofticket').val().replace('tot-2', 'R').replace('tot-1','S'),
//                    Lang: 'TW',
//                    StartStation: $('#select_location01').val(),
//                    EndStation: $('#select_location02').val(),
//                    //StartStationName: $('#startStation').text(),
//                    //EndStationName: $('#endStation').text(),
//                    OutWardSearchDate: $('#Departdate03').val().replace(/\./g,'/'),
//            OutWardSearchTime: $('#outWardTime').val(),
//                    ReturnSearchDate: $('#Returndate03').val().replace(/\./g, '/'),
//            ReturnSearchTime: $('#returnTime').val(),
//                    DiscountType: discountType,
//        }
//                        }).done(function (result) {
//            if (result.success) {
//                app.searchResult(result);
//                app.searched();
//            }
//
//            //20200724 Modified by Eric.Zhuang: 手機板畫面 若為單程 隱藏去程/回程tab
//            if ($('#typesofticket').val().replace('tot-2', 'R').replace('tot-1', 'S') === 'S') {
//
//                $(".xs-tab-panel").css('display', 'none');
//            }
//            else {
//                $(".xs-tab-panel").css('display', '');
//            }
//
//            $('#ttab-02').css('display', '');
//        });
//    }
}
