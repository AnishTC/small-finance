package com.tc.training.smallFinance.filter;

import com.google.common.net.HttpHeaders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.tc.training.smallFinance.dtos.outputs.RoleAndPermissionOutputDto;
import com.tc.training.smallFinance.exception.CustomException;
import com.tc.training.smallFinance.model.User;
import com.tc.training.smallFinance.service.RoleAndPermissionService;
import com.tc.training.smallFinance.service.UserService;
import com.tc.training.smallFinance.utils.CurrentUser;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final UserService userService;

    private final RoleAndPermissionService roleAndPermissionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Creating filter for authentication");
// log.info(request.getHeader());
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) { // remove null this after integration if(false)
// if (false) { // remove null this after integration if(false)
            filterChain.doFilter(request, response);
        } else {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);

            RequestMethod method = RequestMethod.valueOf(request.getMethod());
            String uri = request.getRequestURI();
            uri = uri.replaceAll("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}", "{id}");

            if (method.equals(RequestMethod.OPTIONS)) {
                filterChain.doFilter(request, response);
            } else if (isPublicApi(uri)) {//we need to check public api or not
                filterChain.doFilter(request, response);
            } else {
                if (StringUtils.isNotBlank(token) && (token.startsWith("Bearer "))) {
                    String actualToken = token.split(" ")[1].trim();
                    if (StringUtils.isNotBlank(actualToken)) {
                        try {
                            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(actualToken);
                            String userUid = firebaseToken.getUid();
                            User user = userService.getByFirebaseId(userUid);
                            CurrentUser.set(user);
                            List<RoleAndPermissionOutputDto> allPermissionByMethodAndUrl = roleAndPermissionService.getAllPermissionByMethodAndUrl(method, uri);
                            boolean entry = false;
                            for (RoleAndPermissionOutputDto roles : allPermissionByMethodAndUrl) {
                                if (roles.getRoles().contains(user.getRoleName().toString())) {
                                    filterChain.doFilter(request, response);
                                    entry = true;
                                }
                            }

                            if (!entry) {
                                throw new CustomException("You are not allowed to call this api");
                            }


                        } catch (FirebaseAuthException e) {
                            throw new CustomException("No firebase authentication Token instance");
                        }
                    }
                } else {
                    log.error("No token sent or blank token sent");
                    throw new CustomException("No token sent or blank token sent");
                }
            }
        }
    }

    private boolean isPublicApi(String uri) {
        final List<String> publicApis = List.of("/users/login", "/Account/create");
        return publicApis.contains(uri);
    }

}

