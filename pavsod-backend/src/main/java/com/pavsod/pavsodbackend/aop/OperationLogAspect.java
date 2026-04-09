package com.pavsod.pavsodbackend.aop;

import com.pavsod.pavsodbackend.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class OperationLogAspect {
    public Object logOperation(ProceedingJoinPoint pjp) throws Throwable{

        return 1;
    }

    private Long getCurrentUserId(){
        return CurrentHolder.getCurrentId();
    }
}
