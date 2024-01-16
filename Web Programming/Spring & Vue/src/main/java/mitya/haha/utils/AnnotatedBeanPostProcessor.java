package mitya.haha.utils;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Arrays;

@Component
public class AnnotatedBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (Arrays.stream(beanClass.getMethods()).anyMatch(method -> method.isAnnotationPresent(RoleGuard.class))) {
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), new RoleGuardInvocationHandler(bean));
        }
        return bean;
    }

}
