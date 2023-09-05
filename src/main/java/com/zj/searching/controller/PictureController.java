package com.zj.searching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.zj.searching.annotation.AuthCheck;
import com.zj.searching.common.BaseResponse;
import com.zj.searching.common.DeleteRequest;
import com.zj.searching.common.ErrorCode;
import com.zj.searching.common.ResultUtils;
import com.zj.searching.constant.UserConstant;
import com.zj.searching.exception.BusinessException;
import com.zj.searching.exception.ThrowUtils;
import com.zj.searching.model.dto.picture.PictureQueryRequest;
import com.zj.searching.model.dto.post.PostAddRequest;
import com.zj.searching.model.dto.post.PostEditRequest;
import com.zj.searching.model.dto.post.PostQueryRequest;
import com.zj.searching.model.dto.post.PostUpdateRequest;
import com.zj.searching.model.entity.Picture;
import com.zj.searching.model.entity.Post;
import com.zj.searching.model.entity.User;
import com.zj.searching.model.vo.PostVO;
import com.zj.searching.service.PictureService;
import com.zj.searching.service.PostService;
import com.zj.searching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;

    private final static Gson GSON = new Gson();

    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPostVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        String searchText = pictureQueryRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
        return ResultUtils.success(picturePage);
    }
}
