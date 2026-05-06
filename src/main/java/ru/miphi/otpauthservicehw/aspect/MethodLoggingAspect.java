package ru.miphi.otpauthservicehw.aspect;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class MethodLoggingAspect {

    @Pointcut("within(ru.miphi.otpauthservicehw.controller..*)")
    public void controllerLayer() {}

    @Pointcut("within(ru.miphi.otpauthservicehw.service..*)")
    public void serviceLayer() {}

    @Pointcut("within(ru.miphi.otpauthservicehw.repository..*)")
    public void repositoryLayer() {}

    @Around("controllerLayer()")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "КОНТРОЛЛЕРА");
    }

    @Around("serviceLayer()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "СЕРВИСА");
    }

    @Around("repositoryLayer()")
    public Object logRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethod(joinPoint, "РЕПОЗИТОРИЯ");
    }

    @Before("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void logScheduledMethodStart(@Nonnull JoinPoint joinPoint) {
        String className = joinPoint.getSignature()
                .getDeclaringType()
                .getSimpleName();

        String methodName = joinPoint.getSignature()
                .getName();

        log.info("ЗАПУЩЕН ШЕДУЛЕР: {}.{}", className, methodName);
    }

    private Object logMethod(
            @Nonnull ProceedingJoinPoint joinPoint,
            String layer
    ) throws Throwable {
        String className = joinPoint.getSignature()
                .getDeclaringType()
                .getSimpleName();

        String methodName = joinPoint.getSignature()
                .getName();

        long startTime = System.currentTimeMillis();

        log.info("НАЧАЛО РАБОТЫ МЕТОДА {}: {}.{}", layer, className, methodName);

        try {
            Object result = joinPoint.proceed();

            long durationMs = System.currentTimeMillis() - startTime;

            log.info(
                    "МЕТОД {} УСПЕШНО ЗАВЕРШЁН: {}.{} ВРЕМЯ_ВЫПОЛНЕНИЯ_МС={}",
                    layer,
                    className,
                    methodName,
                    durationMs
            );

            return result;
        } catch (Throwable throwable) {
            long durationMs = System.currentTimeMillis() - startTime;

            log.error(
                    "МЕТОД {} ЗАВЕРШИЛСЯ С ОШИБКОЙ: {}.{} ВРЕМЯ_ВЫПОЛНЕНИЯ_МС={} ОШИБКА={}",
                    layer,
                    className,
                    methodName,
                    durationMs,
                    throwable.getMessage(),
                    throwable
            );

            throw throwable;
        }
    }

}
