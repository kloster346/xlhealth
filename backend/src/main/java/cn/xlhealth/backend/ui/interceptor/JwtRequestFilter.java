package cn.xlhealth.backend.ui.interceptor;

import cn.xlhealth.backend.service.UserSessionService;
import cn.xlhealth.backend.config.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT请求过滤器
 * 从请求中提取JWT token，验证token有效性，提取用户ID并设置Spring Security上下文
 * 注意：此过滤器只负责JWT验证和认证信息设置，不负责用户对象的获取
 */
@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        log.debug("处理请求: {} {}", request.getMethod(), requestURI);

        String userId = null;
        String jwtToken = null;

        // JWT Token格式: "Bearer token"
        if (StringUtils.hasText(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                userId = jwtUtils.getUserIdFromToken(jwtToken);
                log.debug("从JWT token中提取用户ID: {}", userId);
            } catch (Exception e) {
                log.warn("无法从JWT token中获取用户ID: {}", e.getMessage());
            }
        } else {
            log.debug("JWT Token不存在或格式不正确");
        }

        // 验证token并设置认证上下文
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                // 验证JWT token（不需要userId参数，因为token的subject是userId而不是username）
                if (jwtUtils.validateToken(jwtToken)) {
                    log.debug("JWT token验证成功，用户ID: {}", userId);

                    // 检查会话是否有效
                    if (userSessionService.isSessionValid(jwtToken)) {
                        log.debug("用户会话有效，用户ID: {}", userId);

                        // 创建认证对象
                        // 这里可以根据需要添加用户角色/权限
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER"));

                        // 使用用户ID作为principal，这样Controller中可以直接获取用户ID
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userId, null, authorities);

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置Spring Security上下文
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("已设置Spring Security认证上下文，用户ID: {}", userId);
                    } else {
                        log.warn("用户会话无效或已过期，用户ID: {}", userId);
                    }
                } else {
                    log.warn("JWT token验证失败，用户ID: {}", userId);
                }
            } catch (Exception e) {
                log.error("JWT token验证过程中发生错误: {}", e.getMessage(), e);
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 跳过不需要JWT验证的请求
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 跳过公开端点
        return path.startsWith("/api/v1/auth/register") ||
                path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/validate") ||
                path.startsWith("/api/v1/health") ||
                path.startsWith("/actuator") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");
    }
}