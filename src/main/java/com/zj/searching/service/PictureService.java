package com.zj.searching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.searching.model.entity.Picture;

import java.util.List;

/**
 * 图片服务
 */
public interface PictureService {

    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);
}