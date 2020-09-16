package com.snxj.nomark;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClipBoardUtil {

    public static String getUrl(String url) {
        Pattern pattern = Pattern.compile("https://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(url);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            buffer.append(matcher.group());
            buffer.append("\r\n");
        }
        Log.i("url", "getUrl: ==" + (buffer.toString()));
        return buffer.toString();
    }

    /**
     * 获取剪切板内容
     *
     * @return
     */
    public static String paste() {
        ClipboardManager manager = (ClipboardManager) BaseApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
                String addedTextString = String.valueOf(addedText);
                //douyin.com   kuaishou.com   toutiao  weishi  pipix   huoshan
                if (!TextUtils.isEmpty(addedTextString)
                        && addedTextString.contains("http")
                        && (addedTextString.contains("douyin.com")
                        || addedTextString.contains("kuaishou.com")
                        || addedTextString.contains("toutiao")
                        || addedTextString.contains("weishi")
                        || addedTextString.contains("pipix")
                        || addedTextString.contains("huoshan"))) {


                    return addedTextString;
                }
            }
        }
        return null;
    }

    /**
     * 清空剪切板
     */
    public static void clear() {
        ClipboardManager manager = (ClipboardManager) BaseApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            try {
                manager.setPrimaryClip(manager.getPrimaryClip());
                manager.setPrimaryClip(ClipData.newPlainText("", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
