package com.zj.searching;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zj.searching.model.entity.Picture;
import com.zj.searching.model.entity.Post;
import com.zj.searching.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CrawlerText {

    @Resource
    private PostService postService;

    @Test
    void testDynamicFetch() throws IOException {
        int current = 1;
        // 拼接爬取的地址
        String url = "https://cn.bing.com/images/search?q=%E7%8C%AB%E5%92%AA&form=HDRSC2&first=" + current;
        Document document = Jsoup.connect(url).get();
//        System.out.println(document);
        Elements elements =  document.select(".iuscp.isv");
//        System.out.println(elements);
        List<Picture> picRes = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（url）
            // 取iuscp isv元素的第一个（get0）.iusc类的元素的属性m的信息
            String picUrl = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(picUrl, Map.class);
            String mUrl = (String) map.get("murl");
//            System.out.println(mUrl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
//            System.out.println(title);
            Picture pic = new Picture();
            pic.setUrl(mUrl);
            pic.setTitle(title);
            picRes.add(pic);
        }
        System.out.println(picRes);
    }

    @Test
    void testFetchPassage() {
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"问答\",\"reviewStatus\":1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String res = HttpRequest.post(url)
                .body(json).
                execute().
                body();
        Map<String, Object> resMap = JSONUtil.toBean(res, Map.class);
        JSONObject data = (JSONObject) resMap.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for(Object post : records) {
            JSONObject postObj = (JSONObject) post;
            Post obj = new Post();
            obj.setTitle(postObj.getStr("title"));
            obj.setContent(postObj.getStr("content"));
            JSONArray tags = (JSONArray) postObj.get("tags");
            List<String> tagList = tags.toList(String.class);
            obj.setTags(JSONUtil.toJsonStr(tagList));
            obj.setUserId(1L);
            postList.add(obj);
        }
        boolean batch = postService.saveBatch(postList);
        Assertions.assertTrue(batch);
    }
}