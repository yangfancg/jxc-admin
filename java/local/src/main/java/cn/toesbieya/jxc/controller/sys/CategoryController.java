package cn.toesbieya.jxc.controller.sys;

import cn.toesbieya.jxc.model.entity.SysCategory;
import cn.toesbieya.jxc.model.vo.search.CategorySearch;
import cn.toesbieya.jxc.service.sys.SysCategoryService;
import cn.toesbieya.jxc.model.vo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("system/category")
public class CategoryController {
    @Resource
    private SysCategoryService service;

    @GetMapping("getAll")
    public Result getAll() {
        return Result.success(service.getAll());
    }

    @PostMapping("search")
    public Result search(@RequestBody CategorySearch vo) {
        return Result.success(service.search(vo));
    }

    @PostMapping("add")
    public Result add(@RequestBody SysCategory category) {
        String errMsg = validateCategoryCreateParam(category);
        if (errMsg != null) return Result.fail(errMsg);

        category.setCtime(System.currentTimeMillis());
        return service.add(category);
    }

    @PostMapping("update")
    public Result update(@RequestBody SysCategory category) {
        String errMsg = validateCategoryUpdateParam(category);
        if (errMsg != null) return Result.fail(errMsg);

        return service.update(category);
    }

    @PostMapping("del")
    public Result del(@RequestBody SysCategory category) {
        if (category.getId() == null) return Result.fail("删除失败，参数错误");
        return service.del(category);
    }

    private String validateCategoryCreateParam(SysCategory category) {
        if (category.getPid() == null) return "创建失败，参数错误";
        if (StringUtils.isEmpty(category.getName())) return "创建失败，分类名称不能为空";
        if (category.getType() == null) return "创建失败，分类类型不能为空";
        return null;
    }

    private String validateCategoryUpdateParam(SysCategory category) {
        if (category.getId() == null) return "修改失败，参数错误";
        if (category.getPid() == null) return "创建失败，参数错误";
        if (StringUtils.isEmpty(category.getName())) return "创建失败，分类名称不能为空";
        if (category.getType() == null) return "创建失败，分类类型不能为空";
        return null;
    }
}
