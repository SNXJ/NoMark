package com.snxj.nomark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView tv_url;
    EditText etUrl;
    String videoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_url = findViewById(R.id.tv_url);
        etUrl = findViewById(R.id.et_url);
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
        Log.i(TAG, "goCleanWater: =========" + clipText);
        if (clipText == null) {
            return;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        getClipboardData();
    }

    private void getClipboardData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            getClipText();
        } else {
            this.getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //把获取到的内容打印出来
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
            etUrl.setText(clipText);

        } else {
            tv_url.setText("去复制个短视频链接吧");
        }
    }




//    public void parseUrl(String url) {
////        LogUtil.i("clean==", "======url=====" + url);
//        HashMap<String, String> headers = new HashMap<String, String>();
//        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 9.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36");
////        headers.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
////        String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
////        String body = HttpUtil.createGet(redirectUrl).addHeaders(headers).execute().body();
////        Document doc = Jsoup.parse(body);
////        LogUtil.i("clean==", "======redirectUrl=====" + redirectUrl);
//        if (url.contains("kuaishou.com")) {//
//            String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
//            String body = HttpUtil.createGet(redirectUrl).addHeaders(headers).execute().body();
//            Document doc = Jsoup.parse(body);
//            Elements scripts = doc.select("script");
//            for (Element script : scripts) {
//                if (script.html().contains("window.pageData=")) { //注意这里一定是html(), 而不是text()
//                    String jsonStr = script.data().replace("window.pageData=", "");
//                    JSONObject jsonObject = new JSONObject(jsonStr);
//                    JSONObject video = jsonObject.getJSONObject("video");
//                    String srcNoMark = (String) video.get("srcNoMark");
//                    videoUrl = srcNoMark;
//
//                }
//            }
//        } else if (url.contains("douyin.com")) {
//            String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
//            try {
//                String[] queryParts = redirectUrl.split("/");
//                if (null != queryParts) {
//                    videoUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + queryParts[5];
//                    String body = HttpUtil.createGet(videoUrl).addHeaders(headers).execute().body();
//                    JSONObject jsonBody = new JSONObject(body);
//                    JSONArray urlList = ((JSONObject) jsonBody.getJSONArray("item_list").get(0))
//                            .getJSONObject("video")
//                            .getJSONObject("play_addr")
//                            .getJSONArray("url_list");
//                    videoUrl = ((String) urlList.get(0)).replace("playwm", "play");
//                    videoUrl = HttpUtil.createGet(videoUrl).addHeaders(headers).execute().header("Location");
//                }
//            } catch (Exception e) {
//
//            }
//        } else if (url.contains("huoshan.com")) {
//
//        } else if (url.contains("weishi.qq.com")) {
//
//
//            String[] queryParts = url.split("/");
//            if (null != queryParts) {
//                String feedid = queryParts[5];
////                LogUtil.d(TAG, "parseUrl: ====param=====" + feedid);
////                float min = 0.0000001f;
////                float max = 0.9f;
//                double t = Math.random();
////                videoUrl = "https://h5.weishi.qq.com/webapp/json/weishi/WSH5GetPlayPage?t=0.27428460586398&g_tk=";
//                videoUrl = "https://h5.weishi.qq.com/webapp/json/weishi/WSH5GetPlayPage?t=" + t + "&g_tk=";
//
//                Map<String, Object> map = new TreeMap<>();
//                Map<String, Object> map2 = new TreeMap<>();
//                map.put("datalvl", "all");
//                map.put("feedid", feedid);
//                map.put("recommendtype", "0");
//                map.put("_weishi_mapExt", map2);
//
//                String body = HttpUtil.createPost(videoUrl).form(map).addHeaders(headers).execute().body();
//
//                JSONObject bodyJson = new JSONObject(body);
//                videoUrl = ((JSONObject) bodyJson.getJSONObject("data").getJSONArray("feeds").get(0)).get("video_url").toString();
//
//            }
//
//
//        }
//
//
//        /**===========================*/
//        ThreadManager.runOnUi(new Runnable() {
//            @Override
//            public void run() {
//                LogUtil.i(TAG, "goCleanWater: ===NoMark===" + videoUrl);
//                if (videoUrl == null) {
//                    showToast("解析失败");
//                    return;
//                }
//                go2play();
//            }
//        });
//    }




}
