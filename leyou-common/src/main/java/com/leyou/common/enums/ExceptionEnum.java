package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {

    BRAND_NOT_FIND(404, "未查询到商品品牌"),
    CATEGORY_NOT_FIND(404, "未查询到商品分类"),
    SAVE_BRAND_ERROR(500, "新增品牌失败"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    INVALID_FILE_TYPE(400, "无效的文件类型"),
    UPDATE_BRAND_ERROR(500, "更新品牌失败"),
    SPEC_GROUP_NOT_FIND(404, "未查询到规格组"),
    SPEC_PARAM_NOT_FIND(404, "未查询到规格参数"),
    SAVE_SPEC_GROUP_ERROR(500, "新增规格组失败"),
    GOODS_NOT_FIND(404, "未查询到商品"),
    GOODS_UPDATE_ERROR(500, "商品更新错误"),
    SAVE_GOOD_ERROR(500, "新增商品失败"),
    GOODS_ID_CANNOT_BE_NULL(400, "商品id不能为空"),
    INVALID_USER_DATA_TYPE(400, "用户数据类型无效"),
    SAVE_USER_ERROR(500, "新增用户失败"),
    USER_NOT_FIND(404, "用户不存在"),
    QUERY_USER_ERROR(404, "查询用户失败"),
    AUTH_ERROR(500, "授权出错"),
    CART_NOT_FOUND(404, "购物车为空"),
    CREAT_ORDER_ERROR(500, "创建订单失败"),
    STOCK_NOT_ENOUGH(500, "库存不足"),
    ORDER_NOT_FOUND(404, "订单不存在"),
    ORDER_DETAIL_NOT_FOUND(404, "订单详情不存在"),
    ORDER_STATUS_NOT_FOUND(404, "订单状态不存在")
    ;
    private Integer code;
    private String msg;
}
