package com.zj.searching.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zj.searching.model.entity.Picture;
import com.zj.searching.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 聚合搜索视图
 *
 * @author 张瑾一
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    private static final long serialVersionUID = 1L;
}
