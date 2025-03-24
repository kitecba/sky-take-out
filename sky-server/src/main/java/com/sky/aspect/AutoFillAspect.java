package com.sky.aspect;

import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import com.sky.myannotation.AutoFill;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {
    private static final Logger log = LoggerFactory.getLogger(AutoFillAspect.class);

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.myannotation.AutoFill)" )
    public void autoFillpointcut() {}

    @Before("autoFillpointcut()")
    public void before(JoinPoint joinPoint) {
log.info("公共字段填充开始执行");
        //获取对应操作数据库的类型
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        AutoFill annotation = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = annotation.value();
        //获取到当前方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];
        //获取公共填充字段的值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //通过反射为相应的对象属性赋值
        if (value == OperationType.INSERT) {
            try {
                Method setCreateUser = arg.getClass().getDeclaredMethod("setCreateUser",Long.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod("setUpdateUser",Long.class);
                Method setCreateTime = arg.getClass().getDeclaredMethod("setCreateTime",LocalDateTime.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);

                setCreateUser.setAccessible(true);
                setUpdateUser.setAccessible(true);
                setCreateTime.setAccessible(true);
                setUpdateTime.setAccessible(true);

                setCreateUser.invoke(arg,currentId);
                setUpdateUser.invoke(arg,currentId);
                setCreateTime.invoke(arg,now);
                setUpdateTime.invoke(arg,now);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }else if (value == OperationType.UPDATE) {
            try {
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class );
                Method setUpdateTime = arg.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
               //setUpdateUser.setAccessible(true);
                //setUpdateTime.setAccessible(true);
                setUpdateUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}
