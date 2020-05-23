package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     *
     * @param page
     * @param search
     * @param descending
     * @param rows
     * @param sortBy
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "key", required = false) String search,
            @RequestParam(value = "desc", defaultValue = "false") Boolean descending,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy
    ) {
        return ResponseEntity.ok(brandService.queryBrandByPage(page, search, descending, rows, sortBy));
    }

    /**
     * 新增商品分类和品牌
     *
     * @param cids
     * @param brand
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrandCategory(@RequestParam("cids") List<Long> cids, Brand brand) {
        brandService.saveBrandCategory(cids, brand);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品分类和品牌信息
     *
     * @param cids
     * @param id
     * @param name
     * @param image
     * @param letter
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrandCategory(@RequestParam("cids") List<Long> cids, @RequestParam("id") Long id,
                                                    @RequestParam("name") String name, @RequestParam("image") String image,
                                                    @RequestParam("letter") Character letter) {
        brandService.updateBrandCategory(cids, id, name, image, letter);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 通过品牌id删除品牌信息
     *
     * @param bid
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBrandByBid(@PathVariable("id") Long bid) {
        brandService.deleteBrandByBid(bid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据cid查询品牌
     *
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /**
     * 根据bid查询品牌
     *
     * @param bid
     * @return
     */
    @GetMapping("{bid}")
    public ResponseEntity<Brand> queryBrandByBid(@PathVariable("bid") Long bid) {
        return ResponseEntity.ok(brandService.queryBrandByBid(bid));
    }


    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }
}
