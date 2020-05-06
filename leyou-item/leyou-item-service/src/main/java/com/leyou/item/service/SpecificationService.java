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

import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

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
}
