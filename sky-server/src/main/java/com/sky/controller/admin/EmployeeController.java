package com.sky.controller.admin;

import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 新增员工的功能
     * @param employeeDtO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDtO) {
        return employeeService.save(employeeDtO);
    }

    /**
     *员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("page")
    public Result page(EmployeePageQueryDTO employeePageQueryDTO){
        return employeeService.page(employeePageQueryDTO);
    }

    /**
     * 启用禁用员工状态
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用禁用员工状态")
    @PostMapping("status/{status}")
    public Result status(@PathVariable Integer status, Long id) {
        log.info("启用禁用员工状态: {},{}", status, id);
        return employeeService.status(status,id);

    }

    /**
     * 根据ID查询员工
     * @param id
     * @return
     */
    @GetMapping("{id}")
public Result get(@PathVariable Long id) {
        return employeeService.get(id);
}

@PutMapping
public Result update(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.update(employeeDTO);
}

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
