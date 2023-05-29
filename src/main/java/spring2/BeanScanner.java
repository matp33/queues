package spring2;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BeanScanner {

    private BeanRegistry beanRegistry;

    public BeanScanner() {
        beanRegistry = new BeanRegistry();
    }

    public  void run () throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            URL currentFile = getClass().getResource("");
            File parentFile = new File(currentFile.getFile());
            while (!parentFile.getName().equals("classes")){
                parentFile = parentFile.getParentFile();
            }


            File[] files = parentFile.listFiles();
            String directoryName = "";
            List<Class> classes = new ArrayList<>();
            for (File rootFile : files) {
                if (rootFile.isDirectory()){
                    directoryName = rootFile.getName();
                    File[] filesInDirectory = rootFile.listFiles();
                    //TODO handle nested packages
                    for (File file : filesInDirectory) {
                        if (file.getName().endsWith(".class")) {
                            Class<?> aClass = getClassFromFile(directoryName, file);
                            classes.add(aClass);
                        }
                    }
                }
                else{
                    classes.add(getClassFromFile("", rootFile));
                }

            }
            for (Class aClass : classes) {
                for (Annotation annotation : aClass.getAnnotations()) {
                    if (annotation.annotationType().equals(Bean.class)){
                        beanRegistry.getBean(aClass);

                    }
                }
            }
        }

    private static Class<?> getClassFromFile(String directoryName, File file) throws ClassNotFoundException {
        String className = file.getName().substring(0, file.getName().indexOf(".class"));
        return Class.forName( directoryName.isBlank()? className: directoryName +"."+className);
    }

}
