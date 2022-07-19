package com.github.catvod.spider;

import android.content.Context;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Zhaoziyuan extends Spider {
    private static final Pattern aliyun = Pattern.compile("(https://www.aliyundrive.com/s/[^\"]+)");
    private PushAgent zhaoziyuan;


    public String detailContent(List<String> list) {
        try {
            Pattern pattern = aliyun;
            if (pattern.matcher(list.get(0)).find()) {
                return zhaoziyuan.detailContent(list);
            }
            Matcher matcher = pattern.matcher(OkHttpUtil.string("https://zhaoziyuan.me/" + list.get(0), null));
            if (!matcher.find()) {
                return "";
            }
            list.set(0, matcher.group(1));
            return zhaoziyuan.detailContent(list);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    public void init(Context context, String str) {
        super.init(context, str);
        zhaoziyuan = new PushAgent();
        zhaoziyuan.init(context,str);
    }


    public String playerContent(String str, String str2, List<String> list) {
        return zhaoziyuan.playerContent(str, str2, list);
    }

    private Pattern regexVid = Pattern.compile("(\\S+)");
    public String searchContent(String key, boolean quick) {
        try {
            if (quick)
                return "";
            String url = "https://zhaoziyuan.me/so?filename=" + URLEncoder.encode(key);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, null));
            JSONObject result = new JSONObject();

            JSONArray videos = new JSONArray();
            Elements list = doc.select("ul.newsList>li>div.li_con");
            for (int i = 0; i < list.size(); i++) {
                Element vod = list.get(i);
                String title = vod.selectFirst("div.news_text>a>h3").text();
                String cover = "";
                String remark = vod.selectFirst("div.news_text>a>p").text();
                Matcher matcher = regexVid.matcher(vod.select("div.news_text>a").attr("href"));
                if (!matcher.find())
                    continue;
                String id = matcher.group(1);
                JSONObject v = new JSONObject();
                //v.put("vod_id", vod.select("div.news_text>a").attr("href"));
                v.put("vod_id", id);
                v.put("vod_name", title);
                v.put("vod_pic", "https://inews.gtimg.com/newsapp_bt/0/13263837859/1000");
                v.put("vod_remarks", remark);
                videos.put(v);
            }

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}