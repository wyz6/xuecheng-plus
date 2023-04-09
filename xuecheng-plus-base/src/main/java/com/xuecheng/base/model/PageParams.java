package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author wyz
 * @date 2023/04/09 17:06
 * @description 分页参数
 */
@Data
@ToString
public class PageParams {
    @ApiModelProperty(value = "当前页码", example = "1")
    private Long pageNo = 1L;

    @ApiModelProperty(value = "每页记录数默认值", example = "10")
    private Long pageSize = 10L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
