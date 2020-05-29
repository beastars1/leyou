package com.leyou.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收购物车数据的dto对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    /**
     * 商品skuId
     */
    private Long skuId;
    /**
     * 购买数量
     */
    private Integer num;
}
