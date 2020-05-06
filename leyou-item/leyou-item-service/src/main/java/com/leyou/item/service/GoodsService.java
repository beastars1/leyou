package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    /**
     * 分页查询Spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<Spu> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        // 分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 通过搜索字段过滤
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title", "%"+key+"%");
        }
        // 通过是否上下架过滤
        if (saleable != null){
            criteria.andEqualTo("saleable", saleable);
        }
        // 默认排序
        example.setOrderByClause("last_update_time DESC");
        // 查询
        List<Spu> spuList = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FIND);
        }
        // 解析分类和品牌的名称
        loadCategoryAndBrandName(spuList);
        // 解析分页结果
        PageInfo<Spu> info = new PageInfo<>(spuList);

        return new PageResult<>(info.getTotal(), spuList);
    }

    /**
     * 加载商品分类和品牌的名字
     * @param spuList
     */
    private void loadCategoryAndBrandName(List<Spu> spuList) {
        for (Spu spu : spuList) {
            // 处理分类名称
            List<Long> list = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
            List<String> names = categoryService.queryCategoryByIds(list)
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));
            // 处理品牌名称
            Brand brand = brandService.queryBrandByBid(spu.getBrandId());
            spu.setBname(brand.getName());
        }
    }

    /**
     * 新增商品
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        // 新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);

        int count = spuMapper.insert(spu);
        if (count != 1){
            throw new LyException(ExceptionEnum.SAVE_GOOD_ERROR);
        }

        // 新增detail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);

        saveSkuAndStock(spu);
    }

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FIND);
        }
        return spuDetail;
    }

    /**
     * 根据spuId查询对应的所有sku
     * @param spuId
     * @return
     */
    public List<Sku> querySkuListBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FIND);
        }
        // 查询对应库存
        skuList.forEach(s -> {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skuList;
    }

    /**
     * 更新商品信息
      * @param spu
     */
    @Transactional
    public void updateGoods(Spu spu) {
        if (spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_ID_CANNOT_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        // 查询以前sku
        List<Sku> skuList = skuMapper.select(sku);
        // 如果以前存在，就删除
        if (!CollectionUtils.isEmpty(skuList)){
            // 删除以前库存
            List<Long> skuIds = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(skuIds);
            // 删除以前sku
            skuMapper.delete(sku);
        }

        // 更新spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);

        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        // 更新spuDetail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count != 1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        // 新增sku和stock
        saveSkuAndStock(spu);
    }

    /**
     * 根据spu添加sku和stock
     * @param spu
     */
    private void saveSkuAndStock(Spu spu) {
        // 定义库存集合
        List<Stock> stockList = new ArrayList<>();

        // 新增sku
        List<Sku> skuList = spu.getSkus();
        for (Sku sku : skuList) {
            sku.setId(null);
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            int count = skuMapper.insert(sku);
            if (count != 1){
                throw new LyException(ExceptionEnum.SAVE_GOOD_ERROR);
            }

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stockList.add(stock);
        }

        // 批量新增库存
        stockMapper.insertList(stockList);
    }
}
