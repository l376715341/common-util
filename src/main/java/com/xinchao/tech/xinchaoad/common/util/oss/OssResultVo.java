package com.xinchao.tech.xinchaoad.common.util.oss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: luhanyu
 * @Date: 2019/3/13 13:43
 * @Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OssResultVo implements Serializable {
    private Integer id;

    private String name;

    private String ossUrl;
}