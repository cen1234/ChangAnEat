package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.entity.Category;
import com.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜品分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info(category.toString());//打印分类

        categoryService.save(category);//保存

        return R.success("菜品分类添加成功");
    }

    /**
     * 分页查询菜品分类
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        log.info(page + "---" + pageSize);

        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器对象 根据sort进行排序
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);

        //执行分页查询
        categoryService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除菜品的分类
     * 注：关于为什么不使用外键【因为为了增加查询的效率 需要人工来维护表之间的关系 而不是通过外键】
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id){
        log.info("删除id = {} 的分类",id);

        categoryService.removeById(id);//根据id进行删除

        return R.success("id = " + id +"的分类删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> exit(@RequestBody Category category){
        log.info("修改分类信息:{}",category);

        categoryService.updateById(category);

        return R.success("修改分类成功");
    }


    /**
     * 查询菜品分类中的菜品或者套餐名称【这段代码在这里是被复用的】
     * @return
     */
    @GetMapping("/list")//此时的参数只有type
    public R<List<Category>> list(Integer type){
        log.info("查询菜品的分类");
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //根据type进行查询 例如查询菜品或者套餐
        queryWrapper.eq(type != null,Category::getType,type);

        //根据排序的升序 和 更新时间的降序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        //将查询出来的结果放入到集合中 因为没有分页 所以提不能用page封装
        List<Category> list = categoryService.list(queryWrapper);

        //返回结果
        return R.success(list);
    }
}
