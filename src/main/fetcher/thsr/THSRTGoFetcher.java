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
/*
https://www.etax.nat.gov.tw/etwmain/etw600w/b01/am
五互實業股份有限公司
高雄市前金區新生里河東路３０４號７樓

涉嫌逃漏稅事實主旨
檢舉五互實業股份有限公司 故意隱匿/造假遺產稅申報所需給國稅局文件，意圖陷害相關繼承人等觸法

涉嫌逃漏稅事實說明
因為傅雪梅小姐是在近期3個月內過世，而且我們遺產的繼承人需要名列所有財產清冊，以利繳納遺產稅金，而遺產中包含了五互的購買資料。
而五互實業股份有限公司一直規避國稅局的審查，迴避並且惡意恐嚇我們要求盡快做移轉，意圖使繼承人等為觸法行為()。
依照 遺產及贈與稅法 第4條、第8條，銀行法 第5-1條，消費者保護法之定型化契約
質疑項目如下 :
1. 五互販售之商品或服務並不符合政府法令規範，並未以契約詳細名列可使用之服務及不提供的服務。
   - 我認為五互觸犯 消費者保護法第17條、第24條、第25條
2. 五互逃漏統一發票稅金。
   - 因我們並未收到五互寄送付款當日之統一發票、並應依法納營業稅。
2. 質疑五互有具收受存款或吸收資金的行為。
   - 我認為五互觸犯 銀行法 第29條、第29-1條 (可能罰則在 銀行法第八章 第125條)
3. 五互給的契約中有明顯不利於消費者的情事，如解約時候解約金低於銀行給予的比例。
   - 依照其他銀行所提供的人壽保險方案(如台新人壽 新享亮利外幣變額年金保險)，明顯於解約期間所領回的解約金大幅低於銀行規定。


契約ST33011279_1287.pdf
ETWF001.20220426191533365.pdf
1.66 MB
成功
五戶調閱名下契約資料概況
ETWF001.20220426191612183.pdf
638.74 kB
成功
申辦成功
陳建志 先生(女士)您好：

台端於財政部高雄國稅局填寫檢舉逃漏稅信箱， 我們很重視您的寶貴意見，您的問題我們已經交由有關單位處理，請耐心等待我們的回復，謝謝!!

信箱類別
檢舉逃漏稅信箱

申辦案號
1110426_011919_PMBX503

受理單位
財政部高雄國稅局

請保留您的案件編號，以便日後查詢案件之用

財政部高雄國稅局敬啟
*/
