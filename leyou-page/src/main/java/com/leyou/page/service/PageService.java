package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        // 查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        // 查询skus
        List<Sku> skus = spu.getSkus();
        // 查询详情
        SpuDetail detail = spu.getSpuDetail();
        // 查询brand
        Brand brand = brandClient.queryBrandByBid(spu.getBrandId());
        // 查询商品分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询规格参数
        List<SpecGroup> specs = specificationClient.queryGroupByCid(spu.getCid3());

        model.put("spu", spu);
        model.put("detail", detail);
        model.put("skus", skus);
        model.put("categories", categories);
        model.put("brand", brand);
        model.put("specGroups", specs);
        return model;
    }

    /**
     * 创建html文件
     *
     * @param spuId
     */
    public void createHtml(Long spuId) {
        // 创建上下文
        Context context = new Context();
        // 把数据加入上下文
        context.setVariables(loadModel(spuId));

        // 创建输出流
        File disk = new File("F:\\develop\\nginx-1.16.1\\html\\item", spuId + ".html");

        if (disk.exists()) {
            disk.delete();
        }

        try (PrintWriter writer = new PrintWriter(disk, "utf-8")) {
            // 生成html
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            log.error("[静态页服务] 生产静态页异常!", e);
        }
    }

    public void deleteIndex(Long spuId) {
        File disk = new File("F:\\develop\\nginx-1.16.1\\html\\item", spuId + ".html");
        if (disk.exists()) {
            disk.delete();
        }
    }
}
