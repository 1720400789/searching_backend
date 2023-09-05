package com.zj.searching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.searching.common.BaseResponse;
import com.zj.searching.common.ErrorCode;
import com.zj.searching.common.ResultUtils;
import com.zj.searching.exception.BusinessException;
import com.zj.searching.exception.ThrowUtils;
import com.zj.searching.model.dto.post.PostQueryRequest;
import com.zj.searching.model.dto.search.SearchRequest;
import com.zj.searching.model.dto.user.UserQueryRequest;
import com.zj.searching.model.entity.Picture;
import com.zj.searching.model.enums.SearchTypeEnum;
import com.zj.searching.model.vo.PostVO;
import com.zj.searching.model.vo.SearchVO;
import com.zj.searching.model.vo.UserVO;
import com.zj.searching.service.PictureService;
import com.zj.searching.service.PostService;
import com.zj.searching.service.UserService;
import javafx.geometry.Pos;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        String type = searchRequest.getType();

        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        // type不为空则查询所有数据
        if (searchTypeEnum == null) {
            CompletableFuture<Page<Picture>> picTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
                return picturePage;
            });

            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                return postVOPage;
            });

            CompletableFuture.allOf(userTask, picTask, postTask).join();
            SearchVO searchVO = new SearchVO();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<Picture> picturePage = picTask.get();
                Page<PostVO> postVOPage = postTask.get();
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return ResultUtils.success(searchVO);
            } catch (Exception e) {
                log.error("查询异常：{}", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            switch (searchTypeEnum) {
                case POST:
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                    searchVO.setPostList(postVOPage.getRecords());
                    break;
                case USER:
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                    searchVO.setUserList(userVOPage.getRecords());
                    break;
                case PICTURE:
                    Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
                    searchVO.setPictureList(picturePage.getRecords());
                    break;
                default:
            }

            return ResultUtils.success(searchVO);
        }
    }
}