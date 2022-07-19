package myframework.utils;

import static org.burningwave.core.assembler.StaticComponentContainer.Fields;

import java.lang.reflect.Field;
import java.util.Collection;

import org.burningwave.core.classes.FieldCriteria;
import myframework.MyInjector;
import myframework.annotations.MyAutowired;

public class InjectionUtil {

	private InjectionUtil() {
		super();
	}

	/**
	 * Perform injection recursively, for each service inside the Client class
	 */
	public static void autowire(MyInjector myInjector, Class<?> classz, Object classInstance)
			throws InstantiationException, IllegalAccessException {
		Collection<Field> fields = Fields.findAllAndMakeThemAccessible(
			FieldCriteria.forEntireClassHierarchy().allThoseThatMatch(field ->
				field.isAnnotationPresent(MyAutowired.class)
			), 
			classz
		);
		for (Field field : fields) {
			Object fieldInstance = myInjector.getBeanInstance(field.getType(), field.getName());
			Fields.setDirect(classInstance, field, fieldInstance);
			autowire(myInjector, fieldInstance.getClass(), fieldInstance);
		}
	}

}
