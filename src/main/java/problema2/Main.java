package problema2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.core.env.Environment;

import problema2.global.Constantes;
import problema2.global.dominio.ApiResponse;
import problema2.global.exception.BadRequestException;
import problema2.global.exception.NotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@Configuration
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        logger.info("Problema 2 in execution");
    }



    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Order(1)
    public static class AuthorizationConfiguration extends WebSecurityConfigurerAdapter {


        @Autowired
        private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/").permitAll()
                    .and().httpBasic().authenticationEntryPoint(apiAuthenticationEntryPoint)
                    .and().cors()
                    .and().csrf().disable();



        }

        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/api/**")
                            .allowedOrigins("*")
                            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                            .allowedHeaders("Authorization", "Content-Type", "Access-Control-Allow-Headers");
                }
            };
        }


    }

    @Component
    public static class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Autowired
        private ObjectMapper jsonMapper;

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

            ApiResponse apiResponse = new ApiResponse();


            apiResponse.setFechaHora(LocalDateTime.now());
            apiResponse.setEstatus(HttpStatus.UNAUTHORIZED.value());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(jsonMapper.writeValueAsString(apiResponse));

        }

    }


    @Configuration
    @EnableAsync
    public class ApiAsyncConfiguration implements AsyncConfigurer {

        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.initialize();

            return threadPoolTaskExecutor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return new SimpleAsyncUncaughtExceptionHandler();
        }

    }


    @Component
    @Order(1)
    public class LoggingFilter extends OncePerRequestFilter {

        private final AtomicLong sequence = new AtomicLong(0);

        @Override
        public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

            Long id = sequence.incrementAndGet();

            if (logger.isInfoEnabled()) {
                StringBuilder entrada = new StringBuilder();

                entrada.append("Incoming message\n");
                entrada.append("  ID: ").append(id).append("\n");
                entrada.append("  Address: ").append(requestWrapper.getRequestURL()).append("\n");
                entrada.append("  Http-Method: ").append(requestWrapper.getMethod());

                if (logger.isDebugEnabled()) {
                    entrada.append("\n");
                    entrada.append("  Headers: ").append(convertHeadersToMap(requestWrapper).toString());

                    if (requestWrapper.getContentType() != null && requestWrapper.getContentType().startsWith("application/json") && requestWrapper.getContentAsByteArray().length > 0) {
                        entrada.append("\n");
                        entrada.append("  Payload: ").append(new String(requestWrapper.getContentAsByteArray()));
                    }

                    logger.debug(entrada.toString());
                } else {
                    logger.info(entrada.toString());
                }
            }

            Long nanoTime = System.nanoTime();

            chain.doFilter(requestWrapper, responseWrapper);

            nanoTime = System.nanoTime() - nanoTime;

            if (logger.isInfoEnabled()) {
                StringBuilder salida = new StringBuilder();

                salida.append("Outgoing message\n");
                salida.append("  ID: ").append(id).append("\n");
                salida.append("  Http-Status: ").append(responseWrapper.getStatus()).append("\n");
                salida.append("  Execution Time: ").append((double) nanoTime / 1000000.00).append(" ms.");

                if (logger.isDebugEnabled()) {
                    salida.append("\n");
                    salida.append("  Headers: ").append(convertHeadersToMap(responseWrapper).toString()).append("\n");

                    if (responseWrapper.getContentType() != null && responseWrapper.getContentType().startsWith("application/json")) {
                        salida.append("  Payload: ").append(getResponsePayload(responseWrapper));
                    }

                    logger.debug(salida.toString());
                } else {
                    logger.info(salida.toString());
                }
            }

            responseWrapper.copyBodyToResponse();
        }

        private Map<String, String> convertHeadersToMap(HttpServletRequest request) {
            Map<String, String> map = new HashMap<>();

            Enumeration headerNames = request.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String key = (String) headerNames.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }

            return map;
        }

        private Map<String, String> convertHeadersToMap(HttpServletResponse response) {
            Map<String, String> map = new HashMap<>();

            response.getHeaderNames().forEach(headerName -> map.put(headerName, response.getHeader(headerName)));

            return map;
        }

        private String getResponsePayload(ContentCachingResponseWrapper responseWrapper) {
            String payload = "";

            try {
                byte[] buffer = responseWrapper.getContentAsByteArray();
                if (buffer.length > 0) {
                    payload = new String(buffer, 0, buffer.length, responseWrapper.getCharacterEncoding());
                }
            } catch (Exception e) {
                logger.error("Error getting response body", e);
            }

            return payload;
        }

    }


    @ControllerAdvice
    public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

        private final Integer CODIGO_ERROR_SISTEMA = 100;


        @Autowired
        private Environment environment;

        @Autowired
        private MessageSource messageSource;



        @ExceptionHandler(BadRequestException.class)
        public final ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException e, WebRequest request) {

            String mensaje = messageSource.getMessage("error." + e.getCodigo() , e.getParametros(), Constantes.LOCALE);


            ApiResponse apiResponse = new ApiResponse();


            apiResponse.setFechaHora(LocalDateTime.now());
            apiResponse.setEstatus(HttpStatus.BAD_REQUEST.value());
            apiResponse.setCodigo(e.getCodigo());
            apiResponse.setMensaje(mensaje);

            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NotFoundException.class)
        public final ResponseEntity<ApiResponse> handleNotFoundException(NotFoundException e, WebRequest request) {
            ApiResponse apiResponse = new ApiResponse();


            apiResponse.setFechaHora(LocalDateTime.now());
            apiResponse.setEstatus(HttpStatus.NOT_FOUND.value());
            apiResponse.setMensaje("The requested resource was not found");

            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }


        @ExceptionHandler(Exception.class)
        public final ResponseEntity<ApiResponse> handleRuntimeException(Exception e, WebRequest request) {



            String mensaje = messageSource.getMessage("error." + CODIGO_ERROR_SISTEMA , null, Constantes.LOCALE);

            String error = e.getClass().getCanonicalName() + ": " + e.getMessage();

            ApiResponse apiResponse = new ApiResponse();


            apiResponse.setFechaHora(LocalDateTime.now());
            apiResponse.setEstatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setMensaje(mensaje);

            if (environment.acceptsProfiles("develop")) {
                apiResponse.setError(error);
            }

            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
