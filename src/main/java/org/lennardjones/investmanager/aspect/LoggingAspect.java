package org.lennardjones.investmanager.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.lennardjones.investmanager.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Aspect for logging purposes.
 *
 * @since 1.6
 * @author lennardjones
 */
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* org.lennardjones.investmanager.controller.*.*(..))")
    public void allMethodsFromController(){}

    @Pointcut("execution(* org.lennardjones.investmanager.service.*.*(..))")
    public void allMethodsFromService(){}

    @Pointcut("execution(* org.lennardjones.investmanager.util.*.*(..))")
    public void allMethodsFromUtil(){}

    @Pointcut("allMethodsFromController() || allMethodsFromService() || allMethodsFromUtil()")
    public void allMethodsFromControllerServiceUtil(){}

    @Around("allMethodsFromControllerServiceUtil()")
    public Object log(ProceedingJoinPoint proceedingJoinPoint) {
        var signature = proceedingJoinPoint.getSignature();
        var className = signature.getDeclaringType().getName();
        var methodName = signature.getName();

        var logger = Logger.getLogger(className);

        var user = new User();
            user.setUsername("Unknown");
        if (SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal() instanceof User userObject) {
            user = userObject;
        }

        /* Creating directory and file to write logs */
        var directory = System.getProperty("user.home") + "/investmanager/";
        var fileName = LocalDate.now() + ".log";
        try {
            Files.createDirectory(Path.of(directory));
        } catch (IOException ignore) {}
        try {
            Files.createDirectory(Path.of(directory, user.getUsername()));
        } catch (IOException ignore) {}
        try {
            Files.createFile(Path.of(directory, user.getUsername(), fileName));
        } catch (IOException ignore) {}

        /* Logging of method entering */
        var pattern = "%h/investmanager/" + user.getUsername() + "/" + LocalDate.now() + ".log";
        logToFileAndConsole(logger, pattern, className, methodName,
                            proceedingJoinPoint.getArgs(), null ,null);

        /* Logging of method exiting and optionally throwing of exception */
        Object returnedValue = null;
        try {
            returnedValue = proceedingJoinPoint.proceed();

            logToFileAndConsole(logger, pattern, className, methodName,
                    null, returnedValue,null);
        } catch (Throwable thrown) {
            logToFileAndConsole(logger, pattern, className, methodName,
                    null, null,thrown);
        }
        return returnedValue;
    }

    private void logToFileAndConsole(Logger logger, String fileDirectoryPattern, String sourceClass, String sourceMethod,
                                     Object[] params, Object result, Throwable thrown) {
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(fileDirectoryPattern, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            if (params != null) {
                logger.entering(sourceClass, sourceMethod, params);
            } else if (result != null) {
                logger.exiting(sourceClass, sourceMethod, result);
            } else if (thrown != null) {
                logger.throwing(sourceClass, sourceMethod, thrown);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileHandler != null) {
                fileHandler.close();
            }
        }
    }
}
