package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 查询规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecGroupByCid(Long cid) {
        SpecGroup specGroups = new SpecGroup();
        specGroups.setCid(cid);
        List<SpecGroup> groupList = specGroupMapper.select(specGroups);
        if (CollectionUtils.isEmpty(groupList)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FIND);
        }
        return groupList;
    }

    public List<SpecParam> querySpecParamList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> paramList = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(paramList)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FIND);
        }
        return paramList;
    }

    public void deleteSpecGroupByGid(Long gid) {
        int i = specGroupMapper.deleteByPrimaryKey(gid);
        if (i != 0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FIND);
        }
    }

    public void saveSpecGroup(Map<String, Object> map) {
        Long cid = Long.parseLong(map.get("cid").toString());
        String name = map.get("name").toString();
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        specGroup.setName(name);
        int count = specGroupMapper.insert(specGroup);
        if (count != 1){
            throw new LyException(ExceptionEnum.SAVE_SPEC_GROUP_ERROR);
        }
    }

    /**
     * 查询规格参数组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> specGroups = querySpecGroupByCid(cid);
        // 查询当前分类下的参数
        List<SpecParam> specParams = querySpecParamList(null, cid, null);

        // 先把规格参数变成map，map的key是规格组id，map的值是组下的所有参数
        Map<Long, List<SpecParam>> map = new HashMap<>();
        for (SpecParam param : specParams) {
            if (!map.containsKey(param.getGroupId())){
                // 这个组id在map中不存在，新增一个list
                map.put(param.getGroupId(), new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param);
        }

        // 填充param到group
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }

        return specGroups;
    }
}
