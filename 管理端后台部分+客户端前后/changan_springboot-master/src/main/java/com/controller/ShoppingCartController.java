package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.R;
import com.entity.ShoppingCart;
import com.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 点击加入购物车 将菜品/套餐添加到购物车中
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(HttpServletRequest request, @RequestBody ShoppingCart shoppingCart){

        log.info("购物车数据:{}",shoppingCart);

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = (Long) request.getSession().getAttribute("userId");
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetMealId,shoppingCart.getSetMealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne != null){
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /**
     * 将对应的商品-1
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<?> delete(HttpServletRequest request,@RequestBody ShoppingCart shoppingCart){
        log.info("从购物车中删除数据");

        Long id = (Long) request.getSession().getAttribute("userId");

        //查询当前用户是否已经添加过该套餐/菜品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);//查询当前用户对应的id

        Long dishId = shoppingCart.getDishId();
        //如果是套餐
        if(dishId == null){
            queryWrapper.eq(ShoppingCart::getSetMealId,shoppingCart.getSetMealId());
        }
        //如果是菜品
        else {
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one == null){
            R.error("不能减少了！");
        }
        else {
            Integer number = one.getNumber();
            if(number < 1){
                return R.error("数量不能减少了");
            }
            one.setNumber(number - 1);
            shoppingCartService.updateById(one);
        }
        return R.success(one);
    }

    /**
     * 清空某个用户的购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> deleteAll(HttpServletRequest request){
        log.info("清空购物车");

        Long id = (Long) request.getSession().getAttribute("userId");

        //删除当前用户的所有购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);

        //执行删除
        shoppingCartService.remove(queryWrapper);

        return R.success("成功清空购物车");
    }

    /**
     * 根据用户的id 查询购物车的所有数据
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpServletRequest request){

        Long id = (Long) request.getSession().getAttribute("userId");

        log.info("查看购物车信息");

        //查询当前用户所有的购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);//用户id
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);//按照创建时间升序

        //返回
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }
}
