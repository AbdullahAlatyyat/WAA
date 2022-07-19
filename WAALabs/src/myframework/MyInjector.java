package myframework;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import myframework.annotations.MyBean;
import myframework.utils.InjectionUtil;
import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.classes.ClassCriteria;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.SearchConfig;

public class MyInjector {
    private Map<Class<?>, Class<?>> diMap;
    private Map<Class<?>, Object> applicationScope;

    private static MyInjector myInjector;

    private MyInjector() {
        super();
        diMap = new HashMap<>();
        applicationScope = new HashMap<>();
    }

    public static void startApplication(Class<?> mainClass) {
        try {
            synchronized (MyInjector.class) {
                if (myInjector == null) {
                    myInjector = new MyInjector();
                    myInjector.initFramework(mainClass);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T getService(Class<T> classz) {
        try {
            return myInjector.getBeanInstance(classz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initFramework(Class<?> mainClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        Class<?>[] classes = getClasses(mainClass.getPackage().getName(), true);
        ComponentContainer componentConatiner = ComponentContainer.getInstance();
        ClassHunter classHunter = componentConatiner.getClassHunter();
        String packageRelPath = mainClass.getPackage().getName().replace(".", "/");
        try (ClassHunter.SearchResult result = classHunter.findBy(SearchConfig.forResources(packageRelPath).by(ClassCriteria.create().allThoseThatMatch(cls -> {
            return cls.getAnnotation(MyBean.class) != null;
        })))) {
            Collection<Class<?>> types = result.getClasses();
            for (Class<?> implementationClass : types) {
                Class<?>[] interfaces = implementationClass.getInterfaces();
                if (interfaces.length == 0) {
                    diMap.put(implementationClass, implementationClass);
                } else {
                    for (Class<?> iface : interfaces) {
                        diMap.put(implementationClass, iface);
                    }
                }
            }

            for (Class<?> classz : classes) {
                if (classz.isAnnotationPresent(MyBean.class)) {
                    Object classInstance = classz.newInstance();
                    applicationScope.put(classz, classInstance);
                    InjectionUtil.autowire(this, classz, classInstance);
                }
            }
        }
        ;

    }

    public Class<?>[] getClasses(String packageName, boolean recursive) throws ClassNotFoundException, IOException {
        ComponentContainer componentConatiner = ComponentContainer.getInstance();
        ClassHunter classHunter = componentConatiner.getClassHunter();
        String packageRelPath = packageName.replace(".", "/");
        SearchConfig config = SearchConfig.forResources(packageRelPath);
        if (!recursive) {
            config.findInChildren();
        }

        try (ClassHunter.SearchResult result = classHunter.findBy(config)) {
            Collection<Class<?>> classes = result.getClasses();
            return classes.toArray(new Class[classes.size()]);
        }
    }


    @SuppressWarnings("unchecked")
    private <T> T getBeanInstance(Class<T> interfaceClass) throws InstantiationException, IllegalAccessException {
        return (T) getBeanInstance(interfaceClass, null);
    }

    public <T> Object getBeanInstance(Class<T> interfaceClass, String fieldName) throws InstantiationException, IllegalAccessException {
        Class<?> implementationClass = getImplimentationClass(interfaceClass, fieldName);

        if (applicationScope.containsKey(implementationClass)) {
            return applicationScope.get(implementationClass);
        }

        synchronized (applicationScope) {
            Object service = implementationClass.newInstance();
            applicationScope.put(implementationClass, service);
            return service;
        }
    }

    private Class<?> getImplimentationClass(Class<?> interfaceClass, final String fieldName) {
        Set<Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream().filter(entry -> entry.getValue() == interfaceClass).collect(Collectors.toSet());
        String errorMessage = "";
        if (implementationClasses == null || implementationClasses.size() == 0) {
            errorMessage = "no implementation found for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
            if (optional.isPresent()) {
                return optional.get().getKey();
            }
        } else if (implementationClasses.size() > 1) {
            final String findBy = fieldName;
            Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy)).findAny();
            if (optional.isPresent()) {
                return optional.get().getKey();
            } else {
                errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName() + " Expected single implementation or make use of @CustomQualifier to resolve conflict";
            }
        }
        throw new RuntimeErrorException(new Error(errorMessage));
    }
}