package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES(#{cid}, #{bid})")
    Integer saveBrandCategory(@Param("cid")Long cid, @Param("bid")Long bid);

    /**
     * 根据品牌的bid删除品牌类别中间表内的有关对应bid的数据
     * @param bid
     * @return
     */
    @Delete("DELETE FROM tb_category_brand WHERE brand_id = #{bid}")
    Integer deleteBrandCategoryByBid(@Param("bid")Long bid);

    @Select("SELECT * FROM tb_brand " +
            "INNER JOIN tb_category_brand ON tb_brand.`id` = tb_category_brand.`brand_id` " +
            "WHERE tb_category_brand.`category_id` = #{cid}")
    List<Brand> queryBrandByCid(@Param("cid")Long cid);
}
