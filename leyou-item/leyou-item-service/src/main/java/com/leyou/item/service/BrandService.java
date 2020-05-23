package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page, String search, Boolean descending, Integer rows, String sortBy) {
        // 分页
        PageHelper.startPage(page, rows);
        // 过滤 WHERE NAME LIKE "%X%" OR letter == "X"
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(search)) {
            example.createCriteria().orLike("name", "%" + search + "%")
                    .orEqualTo("letter", search.toUpperCase());
        }
        // 排序
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (descending ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }

        // 解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);
        long total = info.getTotal();

        return new PageResult<>(total, list);
    }

    @Transactional
    public void saveBrandCategory(List<Long> cids, Brand brand) {
        // 新增 品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.SAVE_BRAND_ERROR);
        }
        // 新增中间表
        for (Long cid : cids) {
            count = brandMapper.saveBrandCategory(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.SAVE_BRAND_ERROR);
            }
        }
    }

    @Transactional
    public void updateBrandCategory(List<Long> cids, Long id, String name, String image, Character letter) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setImage(image);
        brand.setLetter(letter);

        int update = brandMapper.updateByPrimaryKey(brand);
        if (update != 1) {
            throw new LyException(ExceptionEnum.UPDATE_BRAND_ERROR);
        }
        Long bid = brand.getId();
        Integer delete = brandMapper.deleteBrandCategoryByBid(bid);
        if (delete == 0) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        // 新增中间表
        for (Long cid : cids) {
            Integer save = brandMapper.saveBrandCategory(cid, bid);
            if (save != 1) {
                throw new LyException(ExceptionEnum.SAVE_BRAND_ERROR);
            }
        }
    }

    @Transactional
    public void deleteBrandByBid(Long bid) {
        Integer delete = brandMapper.deleteBrandCategoryByBid(bid);
        if (delete == 0) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        int delete2 = brandMapper.deleteByPrimaryKey(bid);
        if (delete2 == 0) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
    }

    public Brand queryBrandByBid(Long bid) {
        Brand brand = brandMapper.selectByPrimaryKey(bid);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brandList = brandMapper.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        return brandList;
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brandList = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        return brandList;
    }
}
