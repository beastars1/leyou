package com.leyou.order.client;

import com.leyou.order.dto.AddressDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {
    public static final List<AddressDTO> addressList = new ArrayList<AddressDTO>(){
        {
            AddressDTO address = new AddressDTO();
            address.setId(1L);
            address.setAddress("航头镇航头路18号传智播客3号楼");
            address.setCity("上海");
            address.setDistrict("浦东新区");
            address.setName("帅比");
            address.setPhone("15800000000");
            address.setState("上海");
            address.setZipCode("210000");
            address.setIsDefault(false);
            add(address);

            AddressDTO address2 = new AddressDTO();
            address2.setId(2L);
            address2.setAddress("马路边大桥下小墙角一楼");
            address2.setCity("东百");
            address2.setDistrict("家苑");
            address2.setName("啊棍");
            address2.setPhone("15066666666");
            address2.setState("东百");
            address2.setZipCode("666666");
            address2.setIsDefault(true);
            add(address2);
        }
    };

    public static AddressDTO findById(Long id){
        for (AddressDTO addressDTO : addressList) {
            if (addressDTO.getId() == id)
                return addressDTO;
        }
        return null;
    }
}
