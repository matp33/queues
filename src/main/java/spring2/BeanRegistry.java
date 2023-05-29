package spring2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class BeanRegistry {

    private Map<Class, Object> beans = new HashMap<>();

    public Object getBean (Class classType) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Object bean = beans.get(classType);
        if (bean == null){
            Constructor[] constructors = classType.getConstructors();
            if (constructors.length >1){
                throw new UnsupportedOperationException("Bean can have only 1 constructor");
            }
            Constructor constructor = constructors[0];
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


    private Object handleParameterizedConstructor(Constructor constructor) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Object> parameters = new ArrayList<>();
        for (Parameter parameter : constructor.getParameters()) {
            Class parameterClass = Class.forName(parameter.getParameterizedType().getTypeName());
            for (Annotation annotation : parameterClass.getAnnotations()) {
                if (annotation.annotationType().equals(Bean.class)){
                    parameters.add( getBean(parameterClass));
                }
            }
        }
        return constructor.newInstance(parameters.toArray());
    }


}