package com.zj.searching.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zj.searching.esdao.PostEsDao;
import com.zj.searching.model.dto.post.PostEsDTO;
import com.zj.searching.model.entity.Post;
import com.zj.searching.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 *
 * @author 张瑾一
 */
// todo 取消注释开启任务, 开启后每次项目启动都会执行一次，不想它执行了就把@Component注释掉
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    @Override
    public void run(String... args) {
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
        if (batch) {
            log.warn("爬取数据载入数据库成功");
        } else {
            log.warn("爬取数据载入数据库失败！！！");
        }
    }
}
