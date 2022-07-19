package com.github.catvod.spider;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;

import org.json.JSONArray;
import org.json.JSONObject;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XPathEgg extends XPath {

    void loadByWebView(String flagUrl, String loadUrl, String playId) {
        wvHtml = "";
        wvVodUrl = "";
        Activity act = Init.lastCreateActivity;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView(act);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        String msg = consoleMessage.message();
                        if (msg.startsWith("html:")) {
                            wvHtml = msg;
                            if (!playId.isEmpty()) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.loadUrl("javascript:play(" + playId + ");");
                                    }
                                });
                            }
                        }
                        return super.onConsoleMessage(consoleMessage);
                    }
                });
                webView.setWebViewClient(new WebViewClient() {
                    volatile boolean start = false;
                    volatile boolean found = false;

                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                        if (url.endsWith(".css") || url.endsWith(".woff") || url.endsWith(".png")
                                || url.endsWith(".jpg") || url.endsWith(".jpeg"))
                            return new WebResourceResponse("text/plain", "utf-8",
                                    new ByteArrayInputStream("".getBytes()));
                        SpiderDebug.log(url);
                        if (url.equals(flagUrl) && !start) {
                            start = true;
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // view.loadUrl("javascript:console.log('playlist:'+document.getElementsByClassName('playlists')[0].innerHTML.trim());");
                                    view.loadUrl(
                                            "javascript:console.log('html:'+document.getElementsByTagName('html')[0].outerHTML);");
                                }
                            });
                            return new WebResourceResponse("text/plain", "utf-8",
                                    new ByteArrayInputStream("".getBytes()));
                        }
                        if ((!found && start && playId.isEmpty() && !wvHtml.isEmpty())
                                || (!found && start && Misc.isVideoFormat(url))) {
                            if (!playId.isEmpty()) {
                                wvVodUrl = url;
                            }
                            found = true;
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ((ViewGroup) webView.getParent()).removeView(webView);
                                        webView.stopLoading();
                                        webView.clearHistory();
                                        webView.clearCache(true);
                                        webView.loadUrl("about:blank");
                                        webView.onPause();
                                        webView.removeAllViews();
                                        webView.destroy();
                                        webView.destroyDrawingCache();
                                    } catch (Throwable th) {
                                        th.printStackTrace();
                                    }
                                }
                            });
                        }
                        return super.shouldInterceptRequest(view, url);
                    }
                });
                // ViewGroup.LayoutParams lp = new
                // ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                // ViewGroup.LayoutParams.MATCH_PARENT);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
                act.addContentView(webView, lp);
                webView.loadUrl(loadUrl);
            }
        });
    }

    @Override
    protected String categoryUrl(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        if (Integer.parseInt(pg) <= 1) {
            return rule.getCateUrl().replace("{cateId}", tid);
        }
        return (rule.getCateUrl() + "index_{catePg}.html").replace("{cateId}", tid).replace("{catePg}", pg);
    }

    private volatile String wvHtml = "";
    private volatile String wvVodUrl = "";

    @Override
    public String detailContent(List<String> ids) {
        try {

            String webUrl = rule.getDetailUrl().replace("{vid}", ids.get(0));
            long start = System.currentTimeMillis();
            loadByWebView(rule.getHomeUrl() + "/url.php", webUrl, "");
            while (wvHtml.isEmpty() && System.currentTimeMillis() - start < 30000) {
                Thread.sleep(500);
            }
            JXDocument doc = JXDocument.create(wvHtml);
            JXNode vodNode = doc.selNOne(rule.getDetailNode());

            String cover = "", title = "", desc = "", category = "", area = "", year = "", remark = "", director = "",
                    actor = "";

            title = vodNode.selOne(rule.getDetailName()).asString().trim();
            title = rule.getDetailNameR(title);

            cover = vodNode.selOne(rule.getDetailImg()).asString().trim();
            cover = rule.getDetailImgR(cover);
            cover = Misc.fixUrl(webUrl, cover);

            if (!rule.getDetailCate().isEmpty()) {
                try {
                    category = vodNode.selOne(rule.getDetailCate()).asString().trim();
                    category = rule.getDetailCateR(category);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailYear().isEmpty()) {
                try {
                    year = vodNode.selOne(rule.getDetailYear()).asString().trim();
                    year = rule.getDetailYearR(year);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailArea().isEmpty()) {
                try {
                    area = vodNode.selOne(rule.getDetailArea()).asString().trim();
                    area = rule.getDetailAreaR(area);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailMark().isEmpty()) {
                try {
                    remark = vodNode.selOne(rule.getDetailMark()).asString().trim();
                    remark = rule.getDetailMarkR(remark);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailActor().isEmpty()) {
                try {
                    actor = vodNode.selOne(rule.getDetailActor()).asString().trim();
                    actor = rule.getDetailActorR(actor);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailDirector().isEmpty()) {
                try {
                    director = vodNode.selOne(rule.getDetailDirector()).asString().trim();
                    director = rule.getDetailDirectorR(director);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailDesc().isEmpty()) {
                try {
                    desc = vodNode.selOne(rule.getDetailDesc()).asString().trim();
                    desc = rule.getDetailDescR(desc);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }

            JSONObject vod = new JSONObject();
            vod.put("vod_id", ids.get(0));
            vod.put("vod_name", title);
            vod.put("vod_pic", cover);
            vod.put("type_name", category);
            vod.put("vod_year", year);
            vod.put("vod_area", area);
            vod.put("vod_remarks", remark);
            vod.put("vod_actor", actor);
            vod.put("vod_director", director);
            vod.put("vod_content", desc);

            ArrayList<String> playFrom = new ArrayList<>();

            List<JXNode> fromNodes = doc.selN(rule.getDetailFromNode());
            for (int i = 0; i < fromNodes.size(); i++) {
                String name = fromNodes.get(i).selOne(rule.getDetailFromName()).asString().trim();
                name = rule.getDetailFromNameR(name);
                playFrom.add(name);
            }

            ArrayList<String> playList = new ArrayList<>();
            List<JXNode> urlListNodes = doc.selN(rule.getDetailUrlNode());
            if (urlListNodes.size() == playFrom.size() + 2) {
                urlListNodes.remove(0);
                urlListNodes.remove(urlListNodes.size() - 1);
            }
            for (int i = 0; i < urlListNodes.size(); i++) {
                List<JXNode> urlNodes = urlListNodes.get(i).sel(rule.getDetailUrlSubNode());
                List<String> vodItems = new ArrayList<>();
                for (int j = 0; j < urlNodes.size(); j++) {
                    String name = urlNodes.get(j).selOne(rule.getDetailUrlName()).asString().trim();
                    name = rule.getDetailUrlNameR(name);
                    String pid = urlNodes.get(j).selOne(rule.getDetailUrlId()).asString().trim();
                    pid = ids.get(0) + "_" + rule.getDetailUrlIdR(pid);
                    vodItems.add(name + "$" + pid);
                }
                // 排除播放列表为空的播放源
                if (vodItems.size() == 0 && playFrom.size() > i) {
                    playFrom.set(i, "");
                }
                playList.add(TextUtils.join("#", vodItems));
            }
            // 排除播放列表为空的播放源
            for (int i = playFrom.size() - 1; i >= 0; i--) {
                if (playFrom.get(i).isEmpty())
                    playFrom.remove(i);
            }
            for (int i = playList.size() - 1; i >= 0; i--) {
                if (playList.get(i).isEmpty())
                    playList.remove(i);
            }
            for (int i = playList.size() - 1; i >= 0; i--) {
                if (i >= playFrom.size())
                    playList.remove(i);
            }
            String vod_play_from = TextUtils.join("$$$", playFrom);
            String vod_play_url = TextUtils.join("$$$", playList);
            vod.put("vod_play_from", vod_play_from);
            vod.put("vod_play_url", vod_play_url);

            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();
            list.put(vod);
            result.put("list", list);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            String[] info = id.split("_");
            String webUrl = rule.getDetailUrl().replace("{vid}", info[0]);
            long start = System.currentTimeMillis();
            loadByWebView(rule.getHomeUrl() + "/url.php", webUrl, info[1]);
            while (wvVodUrl.isEmpty() && System.currentTimeMillis() - start < 30000) {
                Thread.sleep(500);
            }
            JSONObject result = new JSONObject();
            result.put("url", wvVodUrl);
            result.put("parse", 0);
            result.put("playUrl", "");
            if (!rule.getPlayUa().isEmpty()) {
                result.put("ua", rule.getPlayUa());
            }
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }
}
