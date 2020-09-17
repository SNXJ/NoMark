package com.snxj.nomark;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView tv_url;
    EditText etUrl;
    String videoUrl = null;

    FixedVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_url = findViewById(R.id.tv_url);
        etUrl = findViewById(R.id.et_url);
        videoView = findViewById(R.id.video_view);
        findViewById(R.id.tv_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCleanWater();
            }
        });
    }

    private void goCleanWater() {
        clipText = etUrl.getText().toString().trim();
        if (TextUtils.isEmpty(clipText)) {
//            showToast("请输入视频链接");
            return;
        }
        //获取 url
        clipText = ClipBoardUtil.getUrl(clipText);
        if (clipText == null) {
            return;
        }
        // 自己处理 方便演示
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseUrl(clipText);

            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getClipboardData();
    }

    /**
     * 获取剪切板内容显示出来
     * Q以上 需要延迟
     */
    private void getClipboardData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            getClipText();
        } else {
            this.getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getClipText();
                }
            }, 1500);
        }
    }

    String clipText;

    private void getClipText() {
        clipText = ClipBoardUtil.paste();
        Log.i("=====", "=====clipText======" + clipText);
        if (null != clipText) {
            tv_url.setText(clipText);

            etUrl.setText(ClipBoardUtil.getUrl(clipText));

        } else {
            tv_url.setText("去复制个短视频链接吧");
        }
    }


    public void parseUrl(String url) {
        Log.i("clean==", "======url=====" + url);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 9.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36");
//        headers.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
//        String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
//        String body = HttpUtil.createGet(redirectUrl).addHeaders(headers).execute().body();
//        Document doc = Jsoup.parse(body);
//        LogUtil.i("clean==", "======redirectUrl=====" + redirectUrl);
        if (url.contains("kuaishou.com")) {//
            String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
            String body = HttpUtil.createGet(redirectUrl).addHeaders(headers).execute().body();
            Document doc = Jsoup.parse(body);
            Elements scripts = doc.select("script");
            for (Element script : scripts) {
                if (script.html().contains("window.pageData=")) { //注意这里一定是html(), 而不是text()
                    String jsonStr = script.data().replace("window.pageData=", "");
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONObject video = jsonObject.getJSONObject("video");
                    String srcNoMark = (String) video.get("srcNoMark");
                    videoUrl = srcNoMark;

                }
            }
        } else if (url.contains("douyin.com")) {
            String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
            try {
                String[] queryParts = redirectUrl.split("/");
                if (null != queryParts) {
                    videoUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + queryParts[5];
                    String body = HttpUtil.createGet(videoUrl).addHeaders(headers).execute().body();
                    JSONObject jsonBody = new JSONObject(body);
                    JSONArray urlList = ((JSONObject) jsonBody.getJSONArray("item_list").get(0))
                            .getJSONObject("video")
                            .getJSONObject("play_addr")
                            .getJSONArray("url_list");
                    videoUrl = ((String) urlList.get(0)).replace("playwm", "play");
                    videoUrl = HttpUtil.createGet(videoUrl).addHeaders(headers).execute().header("Location");
                }
            } catch (Exception e) {

            }
        } else if (url.contains("huoshan.com")) {

        } else if (url.contains("weishi.qq.com")) {


            String[] queryParts = url.split("/");
            if (null != queryParts) {
                String feedid = queryParts[5];
//                LogUtil.d(TAG, "parseUrl: ====param=====" + feedid);
//                float min = 0.0000001f;
//                float max = 0.9f;
                double t = Math.random();
//                videoUrl = "https://h5.weishi.qq.com/webapp/json/weishi/WSH5GetPlayPage?t=0.27428460586398&g_tk=";
                videoUrl = "https://h5.weishi.qq.com/webapp/json/weishi/WSH5GetPlayPage?t=" + t + "&g_tk=";

                Map<String, Object> map = new TreeMap<>();
                Map<String, Object> map2 = new TreeMap<>();
                map.put("datalvl", "all");
                map.put("feedid", feedid);
                map.put("recommendtype", "0");
                map.put("_weishi_mapExt", map2);

                String body = HttpUtil.createPost(videoUrl).form(map).addHeaders(headers).execute().body();

                JSONObject bodyJson = new JSONObject(body);
                videoUrl = ((JSONObject) bodyJson.getJSONObject("data").getJSONArray("feeds").get(0)).get("video_url").toString();

            } else {
                Log.i(TAG, "parseUrl: 不支持");
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (videoUrl == null) {
                    Log.i(TAG, "parseUrl: 解析失败");
                    return;
                }

                Log.i(TAG, "goCleanWater: ===videoUrl===" + videoUrl);
                //演示用 界面内 包裹画面的videoView（）
                videoView.post(new Runnable() {
                    @Override
                    public void run() {
                        videoView.setFixedSize(videoView.getWidth(), videoView.getHeight());
                        videoView.invalidate();
                        videoView.setVideoPath(videoUrl);
                        videoView.start();
                    }
                });

            }
        });

//
    }


}
