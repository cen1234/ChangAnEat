package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.common.R;
import com.entity.UserAddress;
import com.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private UserAddressService addressService;

    /**
     * 新增收货地址
     * @param address
     * @return
     */
    @PostMapping
    public R<UserAddress> save(HttpServletRequest request, @RequestBody UserAddress address){//此时传递参数需要用户的id
        Long id = (Long) request.getSession().getAttribute("userId");
        address.setUserId(id);

        log.info("新增收货地址:" + address);

        addressService.save(address);

        return R.success(address);
    }

    /**
     * 保存修改的数据
     * @param address
     * @return
     */
    @PutMapping
    public R<UserAddress> update(@RequestBody UserAddress address){
        addressService.updateById(address);
        return R.success(address);
    }

    /**
     * 删除该地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestParam Long ids){
        addressService.removeById(ids);
        return R.success("该地址删除成功");
    }

    /**
     * 将某个地址设为默认地址
     * @param address
     * @return
     */
    @PutMapping("/default")
    public R<UserAddress> setDefault(HttpServletRequest request,@RequestBody UserAddress address){
        Long id = (Long) request.getSession().getAttribute("userId");

        log.info("设为默认收货地址:" + address);

        //将该用户对应的所有收货地址的默认地址字段都清空
        LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAddress::getUserId,id);
        updateWrapper.set(UserAddress::getIsDefault,0);
        addressService.update(updateWrapper);

        //更新本条数据
        address.setIsDefault(1);
        addressService.updateById(address);

        return R.success(address);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<UserAddress> getDefault(HttpServletRequest request) {
        Long id = (Long) request.getSession().getAttribute("userId");

        log.info("获取默认地址的id是:"+id);

        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, id);
        queryWrapper.eq(UserAddress::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        UserAddress addressBook = addressService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("您没有默认地址");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        UserAddress addressBook = addressService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 根据用户id查询某用户的所有地址
     * @return
     */
    @GetMapping("/list")
    public R<List<UserAddress>> getList(HttpServletRequest request){
        log.info("查询所有地址");

        Long id = (Long) request.getSession().getAttribute("userId");

        if(id == null) return R.error("该用户不存在");

        //查询与用户id相等的所有地址 按照更新时间降序排序
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId,id);
        queryWrapper.orderByDesc(UserAddress::getUpdateTime);
        List<UserAddress> list = addressService.list(queryWrapper);

        return R.success(list);
    }
}
