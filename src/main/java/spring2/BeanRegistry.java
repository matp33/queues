package spring2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class BeanRegistry {

    private Map<Class<?>, Object> beans = new HashMap<>();

    private static BeanRegistry beanRegistry;

    static BeanRegistry getInstance (){
        if (beanRegistry == null){
            beanRegistry = new BeanRegistry();
        }
        return beanRegistry;
    }

    private BeanRegistry (){

    }

    public Object getBean (Class<?> classType) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Object bean = beans.get(classType);
        if (bean == null){
            Constructor<?>[] constructors = classType.getConstructors();
            if (constructors.length >1){
                throw new UnsupportedOperationException("Bean can have only 1 constructor");
            }
            Constructor<?> constructor = constructors[0];
            if (constructor.getParameters().length==0){
                bean = constructor.newInstance();

            }
            else{
                bean = handleParameterizedConstructor( constructor);

            }
            beans.put(classType, bean);

        }
        return bean;
    }


    private Object handleParameterizedConstructor(Constructor<?> constructor) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Object> parameters = new ArrayList<>();
        for (Parameter parameter : constructor.getParameters()) {
            Class<?> parameterClass = Class.forName(parameter.getParameterizedType().getTypeName());
            boolean hasBeanAnnotation = false;
            for (Annotation annotation : parameterClass.getAnnotations()) {
                if (annotation.annotationType().equals(Bean.class)){
                    parameters.add( getBean(parameterClass));
                    hasBeanAnnotation = true;
                    break;
                }
            }
            if (!hasBeanAnnotation){
                throw new IllegalArgumentException("Class has a dependency that is not annotated with @Bean");
            }
        }
        return constructor.newInstance(parameters.toArray());
    }

    public static <T> T getBeanByClass(Class<T> classType){
        //TODO many cyclic dependencies or dependencies from non bean objects to beans - this method should be deleted
        BeanRegistry beanRegistry = BeanRegistry.getInstance();
        T bean;
        try {
            bean = (T)beanRegistry.getBean(classType);
        } catch (NoSuchMethodException | InvocationTargetException | ClassNotFoundException | IllegalAccessException |
                 InstantiationException e) {
            throw new RuntimeException("Bean of class: "+classType+ " not found");
        }
        return bean;
    }


}
