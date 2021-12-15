package com.czsc.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil {

    public static JSONObject getXiuShiData() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String data = httpClientUtil.httpGetRequest("http://api.money.126.net/data/feed/MARKET_HS?callback=ne32194b8878498");
        data = unicodeToString(data);
        return JSONObject.parseObject(data);
    }

    public static JSONObject getTingPanData() {
        String url = "http://api.money.126.net/data/feed/TING_PAI_TI_SHI_RANK?callback=ne_{}&[object%20Object]";
        url = url.replaceAll("\\{\\}", new Date().getTime() + "");
        String data = HttpClientUtil.httpGetRequest(url);
        data = unicodeToString(data);
        data = data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
        return JSONObject.parseObject(data);
    }

    public static JSONArray getStockList() {
        JSONArray array = new JSONArray();
        String url = "https://quotes.money.163.com/hs/service/diyrank.php?host=http%3A%2F%2Fquotes.money.163.com%2Fhs%2Fservice%2Fdiyrank.php&page={}&query=STYPE%3AEQA&fields=NO%2CSYMBOL%2CNAME%2CPRICE%2CPERCENT%2CUPDOWN%2CFIVE_MINUTE%2COPEN%2CYESTCLOSE%2CHIGH%2CLOW%2CVOLUME%2CTURNOVER%2CHS%2CLB%2CWB%2CZF%2CPE%2CMCAP%2CTCAP%2CMFSUM%2CMFRATIO.MFRATIO2%2CMFRATIO.MFRATIO10%2CSNAME%2CCODE%2CANNOUNMT%2CUVSNEWS&sort=PERCENT&order=desc&count=1000&type=query";
        int pageIndex = 0;
        while (true) {
            String data = getStockListByPage(pageIndex, url);
            data = unicodeToString(data);
            JSONObject json = JSONObject.parseObject(data);
            JSONArray list = json.getJSONArray("list");
            array.addAll(list);
            pageIndex++;
            if (list.size() != 1000) {
                break;
            }
        }
        return array;
    }

    private static String getStockListByPage(int pageIndex, String url) {
        url = url.replaceAll("\\{\\}", pageIndex + "");
        return HttpClientUtil.httpGetRequest(url);
    }

    /**
     * 获取前复权历史数据 同花顺财经
     * @param symbol
     * @return
     */
    public static JSONObject getStockHistoryData1(String symbol) {
        String url = "http://d.10jqka.com.cn/v6/line/hs_{}/01/all.js";
        url = url.replaceAll("\\{\\}", symbol);
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "http://stockpage.10jqka.com.cn/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
        String data = HttpClientUtil.httpGetRequest(url, headers);
        data = unicodeToString(data);
        data = data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
        return JSONObject.parseObject(data);
    }

    /**
     * 获取后复权历史数据 同花顺财经
     * @param symbol
     * @return
     */
    public static JSONObject getStockHistoryData2(String symbol) {
        String url = "http://d.10jqka.com.cn/v6/line/hs_{}/02/all.js";
        url = url.replaceAll("\\{\\}", symbol);
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "http://stockpage.10jqka.com.cn/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
        String data = HttpClientUtil.httpGetRequest(url, headers);
        data = unicodeToString(data);
        data = data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
        return JSONObject.parseObject(data);
    }
    /**
     * 获取前复权今日数据 同花顺财经
     * @param symbol
     * @return
     */
    public static JSONObject getTodayStockData1(String symbol) {
        String url = "http://d.10jqka.com.cn/v6/line/hs_{}/01/defer/today.js";
        url = url.replaceAll("\\{\\}", symbol);
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "http://stockpage.10jqka.com.cn/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
        String data = HttpClientUtil.httpGetRequest(url, headers);
        data = unicodeToString(data);
        data = data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
        return JSONObject.parseObject(data);
    }
    /**
     * 获取后复权今日数据 同花顺财经
     * @param symbol
     * @return
     */
    public static JSONObject getTodayStockData2(String symbol) {
        String url = "http://d.10jqka.com.cn/v6/line/hs_{}/02/defer/today.js";
        url = url.replaceAll("\\{\\}", symbol);
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "http://stockpage.10jqka.com.cn/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
        String data = HttpClientUtil.httpGetRequest(url, headers);
        data = unicodeToString(data);
        data = data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
        return JSONObject.parseObject(data);
    }


    public static JSONObject getStockHistoryDataFromXQ(String symbol) {
        String url = "http://api.money.126.net/data/feed/0000001,money.api?callback=_ntes_quote_callback18490008";
        url = url.replaceAll("\\{\\}", symbol);
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "http://stockpage.10jqka.com.cn/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
        String data = HttpClientUtil.httpGetRequest(url, headers);
        data = unicodeToString(data);
        data = data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
        return JSONObject.parseObject(data);
    }

    /**
     * 【Unicode转中文】
     * @param unicode
     * @return 返回转码后的字符串 - 中文格式
     */
    public static String unicodeToString(final String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 0; i < hex.length; i++) {
            try {
                // 汉字范围 \u4e00-\u9fa5 (中文)
                if (hex[i].length() >= 4) {//取前四个，判断是否是汉字
                    String chinese = hex[i].substring(0, 4);
                    try {
                        int chr = Integer.parseInt(chinese, 16);
                        boolean isChinese = isChinese((char) chr);
                        //转化成功，判断是否在  汉字范围内
                        if (isChinese) {//在汉字范围内
                            // 追加成string
                            string.append((char) chr);
                            //并且追加  后面的字符
                            String behindString = hex[i].substring(4);
                            string.append(behindString);
                        } else {
                            string.append(hex[i]);
                        }
                    } catch (NumberFormatException e1) {
                        string.append(hex[i]);
                    }
                } else {
                    string.append(hex[i]);
                }
            } catch (NumberFormatException e) {
                string.append(hex[i]);
            }
        }
        return string.toString();
    }

    /**
     * 【判断是否为中文字符】
     *
     * @param c
     * @return 返回判断结果 - boolean类型
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        JSONObject stockHistoryData = getTodayStockData1("600093");
        JSONObject json = stockHistoryData.getJSONObject("hs_" + "600093");
        String date = json.getString("1");
        String open = json.getString("7");
        String high = json.getString("8");
        String low = json.getString("9");
        String close = json.getString("11");
        Integer volumn = json.getInteger("13");

        System.out.println("a");
    }
}
