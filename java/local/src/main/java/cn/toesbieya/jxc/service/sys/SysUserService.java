package cn.toesbieya.jxc.service.sys;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.mapper.SysRoleMapper;
import cn.toesbieya.jxc.mapper.SysUserMapper;
import cn.toesbieya.jxc.model.entity.SysRole;
import cn.toesbieya.jxc.model.entity.SysUser;
import cn.toesbieya.jxc.model.vo.DepartmentVo;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.UserSearch;
import cn.toesbieya.jxc.util.Util;
import cn.toesbieya.jxc.util.WebSocketUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysUserService {
    private static final String DEFAULT_PWD = DigestUtils.md5DigestAsHex("123456".getBytes());
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysDepartmentService departmentService;

    public PageResult<UserVo> search(UserSearch vo) {
        Integer id = vo.getId();
        String name = vo.getName();
        String roleIds = vo.getRole();
        Integer status = vo.getStatus();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        Wrapper<SysUser> wrapper =
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::isAdmin, 0)
                        .eq(id != null, SysUser::getId, id)
                        .like(!StringUtils.isEmpty(name), SysUser::getName, name)
                        .inSql(!StringUtils.isEmpty(roleIds), SysUser::getRole, roleIds)
                        .eq(status != null, SysUser::getStatus, status)
                        .ge(startTime != null, SysUser::getCtime, startTime)
                        .le(endTime != null, SysUser::getCtime, endTime)
                        .orderByDesc(SysUser::getCtime);

        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<SysUser> users = userMapper.selectList(wrapper);
        PageResult<SysUser> pageResult = new PageResult<>(users);

        int userNum = users.size();

        //传递给前端的结果集
        List<UserVo> result = new ArrayList<>(userNum);

        //当前在线的用户id
        Set<Integer> onlineUserIds = WebSocketUtil.getOnlineUserIds();

        //查询得到的用户的角色id
        Set<Integer> userRoleIds = new HashSet<>(userNum);
        //查询得到的用户的部门id
        Set<Integer> userDeptIds = new HashSet<>(userNum);

        users.forEach(user -> {
            result.add(new UserVo(user));
            Integer rid = user.getRole();
            Integer deptId = user.getDept();
            if (rid != null) userRoleIds.add(rid);
            if (deptId != null) userDeptIds.add(deptId);
        });

        List<SysRole> roles = userRoleIds.isEmpty() ? Collections.emptyList() : roleMapper.selectBatchIds(userRoleIds);
        List<DepartmentVo> depts = userDeptIds.isEmpty() ? Collections.emptyList() : departmentService.getAll();

        result.forEach(userVo -> {
            //设置在线情况
            userVo.setOnline(Util.some(onlineUserIds, i -> userVo.getId().equals(i)));

            //设置用户的角色名称
            Integer roleId = userVo.getRole();
            if (roleId != null) {
                SysRole matched = Util.find(roles, i -> i.getId().equals(roleId));
                if (matched != null) {
                    userVo.setRoleName(matched.getName());
                }
            }

            //设置部门名称
            Integer deptId = userVo.getDept();
            if (deptId != null) {
                DepartmentVo matched = Util.find(depts, i -> i.getId().equals(deptId));
                if (matched != null) {
                    userVo.setDeptName(matched.getFullname());
                }
            }
        });

        return new PageResult<>(pageResult.getTotal(), result);
    }

    @UserAction("'添加用户：'+#user.name")
    public Result add(SysUser user) {
        if (isNameExist(user.getName(), null)) {
            return Result.fail("该用户名称已存在");
        }

        user.setId(null);
        user.setCtime(System.currentTimeMillis());
        user.setPwd(DEFAULT_PWD);

        userMapper.insert(user);
        return Result.success("添加成功");
    }

    @UserAction("'修改用户：'+#user.name")
    public Result update(SysUser user) {
        Integer id = user.getId();
        String name = user.getName();

        if (isNameExist(name, id)) {
            return Result.fail("该用户名称已存在");
        }

        userMapper.update(
                null,
                Wrappers.lambdaUpdate(SysUser.class)
                        .set(SysUser::getName, name)
                        .set(SysUser::getRole, user.getRole())
                        .set(SysUser::getDept, user.getDept())
                        .set(SysUser::getStatus, user.getStatus())
                        .eq(SysUser::getId, id)
        );

        return Result.success("修改成功");
    }

    @UserAction("'删除用户：'+#user.name")
    @Transactional(rollbackFor = Exception.class)
    public Result del(SysUser user) {
        int rows = userMapper.delete(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getId, user.getId())
                        .eq(SysUser::isAdmin, 0)
        );
        WebSocketUtil.sendLogoutEvent(Collections.singletonList(user.getId()), "该用户已删除");
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新后重试");
    }

    @UserAction
    public Result kick(List<SysUser> users) {
        WebSocketUtil.sendLogoutEvent(
                users
                        .stream()
                        .map(SysUser::getId)
                        .collect(Collectors.toList()),
                "你已被强制下线，请重新登陆"
        );

        return Result.success("踢出成功");
    }

    @UserAction("'重置用户密码：'+#user.name")
    public Result resetPwd(SysUser user) {
        int rows = userMapper.update(
                null,
                Wrappers.lambdaUpdate(SysUser.class)
                        .set(SysUser::getPwd, DEFAULT_PWD)
                        .eq(SysUser::getId, user.getId())
        );
        return rows > 0 ? Result.success() : Result.fail("重置失败，未匹配到用户");
    }

    //用户名重复时返回true
    private boolean isNameExist(String name, Integer id) {
        Integer num = userMapper.selectCount(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getName, name)
                        .ne(id != null, SysUser::getId, id)
        );
        return num != null && num > 0;
    }
}
