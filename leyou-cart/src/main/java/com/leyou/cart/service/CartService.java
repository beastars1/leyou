package com.leyou.cart.service;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "cart:uid:";

    /**
     * 购物车结构是一个双层Map：Map<String,Map<String,String>>
     * - 第一层Map，Key是用户id
     * - 第二层Map，Key是购物车中商品id，值是购物车数据
     * - Map<string,Map<String,String>>
     */
    public void addCart(Cart cart) {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();
        // hashKey
        String skuId = cart.getSkuId().toString();
        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        // 判断当前购物车商品是否存在
        Integer num = cart.getNum();
        if (operation.hasKey(skuId)) {
            // 存在，修改数量
            String json = operation.get(skuId).toString();
            cart = JsonUtils.toBean(json, Cart.class);
            cart.setNum(cart.getNum() + num);
        }
        // 写回redis
        operation.put(skuId, JsonUtils.toString(cart));
    }

    public List<Cart> queryCartList() {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();

        if (!redisTemplate.hasKey(key)) {
            // 如果key不存在，返回404
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }

        // 获取登录用户的所有购物车商品
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        List<Cart> cartList = operation.values().stream()
                .map(o -> JsonUtils.toBean(o.toString(), Cart.class))
                .collect(Collectors.toList());

        return cartList;
    }

    public void updateNum(Long skuId, Integer num) {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();
        // hashKey
        String hashKey = skuId.toString();
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);

        // 判断是否存在
        if (!operation.hasKey(hashKey)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }

        // 获取购物车
        String json = operation.get(hashKey).toString();
        Cart cart = JsonUtils.toBean(json, Cart.class);
        cart.setNum(num);
        // 写入购物车
        operation.put(hashKey, JsonUtils.toString(cart));
    }

    public void deleteCart(Long skuId) {
        // 获取登录用户
        UserInfo user = UserInterceptor.getUser();
        // key
        String key = KEY_PREFIX + user.getId();

        // 删除
        redisTemplate.opsForHash().delete(key, skuId.toString());
    }

    public void addCartList(List<Cart> carts) {
        for (Cart cart : carts) {
            addCart(cart);
        }
    }
}
