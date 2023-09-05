package com.zj.searching.model.dto.picture;

import com.zj.searching.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {
    private String searchText;

    private static final long serialVersionUID = 1L;
}