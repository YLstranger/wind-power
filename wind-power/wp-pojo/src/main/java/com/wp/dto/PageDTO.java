package com.wp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {

    //页数
    private Integer pageNumber;

    //页码
    private Integer pageSize;
}
