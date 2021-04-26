package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: BrandMapper
 * @Description: TODO
 * @author: LiuGe
 * @date: 2020/7/2  18:27
 */
public interface BrandMapper extends BaseMapper<Brand> {
    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);
    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteCategoryBrand(Long bid);
    @Select("select * from tb_category_brand where brand_id = #{bid}")
    List<Category> queryByBid(Long bid);
    @Insert("replace into tb_category_brand(category_id,brand_id) values(#{cid},#{id})")
    int replaceByBid(@Param("cid") Long cid, @Param("id") Long id);
    @Select("select b.id,b.name,b.image,b.letter from tb_brand b " +
            "inner join tb_category_brand cb on b.id = cb.brand_id where cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);
    @Select("SELECT b.* FROM tb_brand b INNER JOIN tb_category_brand tb ON b.id = tb.brand_id WHERE tb.category_id = #{cid} ")
    List<Brand> selectBrandByCid(Long cid);
}
