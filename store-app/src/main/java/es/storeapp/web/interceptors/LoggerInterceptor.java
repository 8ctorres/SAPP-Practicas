package es.storeapp.web.interceptors;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoggerInterceptor extends HandlerInterceptorAdapter{

    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        logger.trace(MessageFormat.format("Request URL: {0} started at {1}",
                request.getRequestURL(), LocalDateTime.now()));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        logger.trace(MessageFormat.format("Request URL: {0} finished at {1}",
                request.getRequestURL(), LocalDateTime.now()));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) throws Exception {
        logger.trace(MessageFormat.format("After completion URL: {0} finished at {1}",
                request.getRequestURL(), LocalDateTime.now()));
    }

}
