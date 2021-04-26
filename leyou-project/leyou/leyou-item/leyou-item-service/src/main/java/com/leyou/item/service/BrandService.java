package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.emuns.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @ClassName: BrandService
 * @Description: TODO
 * @author: LiuGe
 * @date: 2020/7/2  18:28
 */
@Service
@Slf4j
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            // 查询条件不为空 过滤条件
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        // 排序
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        // 解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);
        return new PageResult<>(list,info.getTotal());
    }

    @Transactional(rollbackFor = {})
    public void saveBrand(Brand brand, List<Long> cids) {
        // 新增品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        // 新增中间表
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
            }
        }
    }

    @Transactional(rollbackFor = {})
    public void updateBrand(Brand brand, List<Long> cids) {
        // 更新品牌
        int count = brandMapper.updateByPrimaryKey(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_UPDATE_ERROR);
        }
        // 更新中间表
        // 先将旧的全部删除
        brandMapper.deleteCategoryBrand(brand.getId());
        // 再插入新的
        for (Long cid : cids) {
            count = brandMapper.replaceByBid(cid, brand.getId());
            if (count < 1) {
                throw new LyException(ExceptionEnum.BRAND_UPDATE_ERROR);
            }
        }

    }

    @Transactional(rollbackFor = {})
    public void deleteBrand(Long bid) {
        // 先从中间表中查询有没有该数据
        int count;
        List<Category> list = brandMapper.queryByBid(bid);
        if (!CollectionUtils.isEmpty(list)) {
            // 确定有,删除中间表中的数据
            count = brandMapper.deleteCategoryBrand(bid);
            if (count < 1) {
                throw new LyException(ExceptionEnum.BRAND_DELETE_ERROR);
            }
        }
        // 删除品牌表中的该品牌
        count = brandMapper.deleteByPrimaryKey(bid);
        if (count < 1) {
            throw new LyException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
    }

    public Brand queryByBrandId(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    public List<Brand> queryByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    public List<Brand> queryBrandsByCid(Long cid) {
        List<Brand> brands = brandMapper.selectBrandByCid(cid);
        return brands;
    }
}
