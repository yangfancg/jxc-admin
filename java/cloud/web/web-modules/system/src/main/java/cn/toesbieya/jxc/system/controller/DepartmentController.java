package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.enumeration.GeneralStatusEnum;
import cn.toesbieya.jxc.common.model.entity.SysDepartment;
import cn.toesbieya.jxc.common.model.vo.DepartmentVo;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.system.service.DepartmentService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("department")
public class DepartmentController {
    @Resource
    private DepartmentService service;

    @GetMapping("get")
    public Result get(boolean all) {
        List<DepartmentVo> list = service.getAll();
        if (!all) {
            list = list
                    .stream()
                    .filter(i -> i.getStatus().equals(GeneralStatusEnum.ENABLED.getCode()))
                    .collect(Collectors.toList());
        }
        return Result.success(list);
    }

    @PostMapping("add")
    public Result add(@RequestBody SysDepartment department) {
        if (null == department.getPid()
                || StringUtils.isEmpty(department.getName())
                || null == department.getStatus()) {
            return Result.fail("添加失败，参数错误");
        }

        department.setId(null);

        return service.add(department);
    }

    @PostMapping("update")
    public Result update(@RequestBody SysDepartment department) {
        if (null == department.getId()
                || null == department.getPid()
                || StringUtils.isEmpty(department.getName())
                || null == department.getStatus()) {
            return Result.fail("修改失败，参数错误");
        }

        return service.update(department);
    }

    @PostMapping("del")
    public Result del(@RequestBody SysDepartment department) {
        if (null == department.getId() || StringUtils.isEmpty(department.getName())) {
            return Result.fail("删除失败，参数错误");
        }

        return service.del(department);
    }
}
