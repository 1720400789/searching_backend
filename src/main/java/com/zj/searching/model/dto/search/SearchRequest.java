package com.zj.searching.model.dto.search;

import com.zj.searching.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 聚合搜索的请求参数类
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchRequest extends PageRequest implements Serializable {
    /**
     * 后端统一搜索词
     */
    private String searchText;

    /**
     * 搜索类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}