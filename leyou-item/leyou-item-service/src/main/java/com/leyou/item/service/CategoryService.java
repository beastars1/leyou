package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    public List<Category> queryCategoryListByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        // mapper会把对象中的非空属性作为查询条件
        List<Category> list = categoryMapper.select(category);
//        if (list == null || list.isEmpty())
        // 判断结果
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FIND);
        }
        return list;
    }

    /**
     * 查询单个商品分类
     * @param bid
     * @return
     */
    public List<Category> queryCategoryByBid(Long bid) {
        List<Category> list = categoryMapper.queryCategoryByBid(bid);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FIND);
        }
        return list;
    }

    /**
     * 通过id数组查询一组商品分类
     * @param ids
     * @return
     */
    public List<Category> queryCategoryByIds(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FIND);
        }
        return list;
    }
}
