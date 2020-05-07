package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(specificationService.querySpecGroupByCid(cid));
    }

    /**
     * 查询参数列表
     * @param gid 组id
     * @param cid 商品id
     * @param searching 是否需要搜索
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParamList(
            @RequestParam(value = "gid", required = false)Long gid,
            @RequestParam(value = "cid", required = false)Long cid,
            @RequestParam(value = "searching", required = false)Boolean searching
            ){
        return ResponseEntity.ok(specificationService.querySpecParamList(gid, cid, searching));
    }

    @DeleteMapping("group/{gid}")
    public ResponseEntity<Void> deleteSpecGroupByGid(@PathVariable("gid")Long gid){
        specificationService.deleteSpecGroupByGid(gid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody Map<String, Object> map){
        specificationService.saveSpecGroup(map);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类cid查询规格参数组，及组内参数
     * @param cid
     * @return
     */
    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@RequestParam("cid")Long cid){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }
}
