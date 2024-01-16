package mitya.haha.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoleGuardInvocationHandler implements InvocationHandler {

    private final Map<String, Method> annotatedMethods = new HashMap<>();
    private final Map<String, Method> allMethods = new HashMap<>();
    private Object target;

    public RoleGuardInvocationHandler(Object target) {
        this.target = target;
        for(Method method: target.getClass().getMethods()) {
            System.out.println(method.getName());
            if(method.isAnnotationPresent(RoleGuard.class)){
                System.out.println(method.getName());
                this.annotatedMethods.put(method.getName(), method);
            }
            this.allMethods.put(method.getName(), method);

        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(annotatedMethods.containsKey(method.getName())) {
            Authentication currAuth = SecurityContextHolder.getContext().getAuthentication();
            String[] allowedRoles = annotatedMethods.get(method.getName()).getAnnotation(RoleGuard.class).roles();
            if(currAuth != null){
                 List<String> intersectingRoles =  Arrays.stream(allowedRoles).
                        filter(x->currAuth.getAuthorities().stream().anyMatch(y -> y.getAuthority().equalsIgnoreCase(x)))
                        .toList();
                 if(intersectingRoles.isEmpty()){
                     throw new NotEnoughAuthorityException("No matching authority was found to execute method.");
                 }

            } else {
                if(allowedRoles.length != 0) {
                    throw new NotEnoughAuthorityException("No authorities were provided for method that require authorities.");
                }
            }
        }

        return allMethods.get(method.getName()).invoke(target, args);
    }
}
