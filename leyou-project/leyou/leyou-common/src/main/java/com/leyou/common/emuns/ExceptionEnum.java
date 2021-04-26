package com.leyou.common.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    NOT_ALLOW_TYPE(500,"文件类型不匹配"),
    CATEGORY_NOT_FOUND(500,"商品不存在"),
    BRAND_NOT_FOUND(500,"品牌不存在"),
    BRAND_SAVE_ERROR(500,"品牌保存失败"),
    BRAND_UPDATE_ERROR(500,"品牌更新失败"),
    GOODS_SKU_NOT_FOUND(500,"商品SKU不存在"),
    BRAND_DELETE_ERROR(500,"品牌删除失败"),
    GOODS_SAVE_ERROR(500,"保存商品失败"),
    SPEC_DETAIL_NOT_FOUND(500,"商品共有规格参数不存在")
    ;
    private int code;
    private String message;

}
