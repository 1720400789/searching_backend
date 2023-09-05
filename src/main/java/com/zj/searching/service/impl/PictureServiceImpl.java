package com.zj.searching.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.searching.common.ErrorCode;
import com.zj.searching.exception.BusinessException;
import com.zj.searching.model.entity.Picture;
import com.zj.searching.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 *
 * @author 张瑾一
 */
@Service
@Slf4j
public class PictureServiceImpl implements PictureService {

    /**
     *
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        // 拼接爬取的地址
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }
        Elements elements =  document.select(".iuscp.isv");
        List<Picture> picRes = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（url）
            // 取iuscp isv元素的第一个（get0）.iusc类的元素的属性m的信息
            String picUrl = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(picUrl, Map.class);
            String mUrl = (String) map.get("murl");
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            Picture pic = new Picture();
            pic.setUrl(mUrl);
            pic.setTitle(title);
            picRes.add(pic);
            if (picRes.size() >= pageSize) {
                break;
            }
        }
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(picRes);
        return picturePage;
    }
}
