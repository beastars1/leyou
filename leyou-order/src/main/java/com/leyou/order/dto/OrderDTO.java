package com.leyou.order.dto;

import com.leyou.common.dto.CartDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单的dto对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    /**
     * 收获人地址id
     */
    @NotNull
    private Long addressId;
    /**
     * 付款类型
     */
    @NotNull
    private Integer paymentType;
    /**
     * 订单详情
     */
    @NotNull
    private List<CartDTO> carts;
}
