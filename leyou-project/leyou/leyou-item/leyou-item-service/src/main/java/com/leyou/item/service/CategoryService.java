package com.leyou.item.service;

import com.leyou.common.emuns.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;


    public List<Category> queryCategoryListByPid(Long pid) {
        // mapper会把对象中的非空属性作为查询条件
        Category t = new Category();
        t.setParentId(pid);
        List<Category> list = categoryMapper.select(t);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<Category> queryByBrandId(Long bid) {
        List<Category> list = categoryMapper.queryByBrandId(bid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

//    public List<Category> queryNamesByIds(List<Long> ids) {
//        List<Category> list = categoryMapper.selectByIdList(ids);
//        if (CollectionUtils.isEmpty(list)) {
//            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
//        }
//        return list;
//    }
public List<String> queryNamesByIds(List<Long> ids) {
    List<Category> list = this.categoryMapper.selectByIdList(ids);
    List<String> names = new ArrayList<>();
    for (Category category : list) {
        names.add(category.getName());
    }
//    return names;
   return list.stream().map(category -> category.getName()).collect(Collectors.toList());
}
}
