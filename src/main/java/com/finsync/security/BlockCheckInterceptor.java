package com.finsync.security;

import com.finsync.model.User;
import com.finsync.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class BlockCheckInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public BlockCheckInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return true;
        }
        try {
            User user = userService.findByEmail(auth.getName());

            if (user.isBlocked()) {
                SecurityContextHolder.clearContext();
                request.getSession().invalidate();
                response.sendRedirect("/auth/login?blocked");
                return false;
            }

            String dbRole = "ROLE_" + user.getRole();
            boolean roleMatches = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(dbRole));
            if (!roleMatches && auth.getPrincipal() instanceof UserDetails oldPrincipal) {
                List<SimpleGrantedAuthority> newAuthorities = List.of(new SimpleGrantedAuthority(dbRole));
                UserDetails newPrincipal = new org.springframework.security.core.userdetails.User(
                        oldPrincipal.getUsername(),
                        oldPrincipal.getPassword(),
                        oldPrincipal.isEnabled(),
                        oldPrincipal.isAccountNonExpired(),
                        oldPrincipal.isCredentialsNonExpired(),
                        !user.isBlocked(),
                        newAuthorities
                );
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                        newPrincipal, auth.getCredentials(), newAuthorities
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
                request.getSession(true).setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext()
                );
            }
        } catch (Exception ignored) {}
        return true;
    }
}
