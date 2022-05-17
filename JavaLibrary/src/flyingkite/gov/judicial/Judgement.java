package flyingkite.gov.judicial;

public class Judgement {
    public String title;
    public String date;
    public String reason;
    public String desc;
    public String contextLink;

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s\n%s", title, date, reason, desc, contextLink);
    }
}
