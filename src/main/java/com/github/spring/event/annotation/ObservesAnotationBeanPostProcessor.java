package com.github.spring.event.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import com.github.spring.event.Event;
import com.github.spring.event.EventRegistry;
import com.github.spring.event.QualifiedEvent;

public class ObservesAnotationBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

	private EventRegistry eventRegistry;

	private static final Logger LOG = LoggerFactory.getLogger(ObservesAnotationBeanPostProcessor.class);

	/**
	 * The order. Must be placed between AutowiredAnnotationBeanPostProcessor and AnnotationTransactionAttributeSource.
	 */
	private int order = Ordered.LOWEST_PRECEDENCE;

	public ObservesAnotationBeanPostProcessor() {

	}

	/**
	 * Sets the order.
	 * 
	 * @param order
	 *            the new order
	 */
	public void setOrder(final int order) {
		this.order = order;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		return this.order;
	}

	public void setEventRegistry(EventRegistry eventRegistry) {
		this.eventRegistry = eventRegistry;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
			@SuppressWarnings("rawtypes")
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (!field.getType().isAssignableFrom(Event.class)) {
					return;
				}
				Annotation[] annotations = field.getAnnotations();
				for (Annotation ann : annotations) {
					if (ann.annotationType().isAnnotationPresent(Qualifier.class)) {

						LOG.info("found qualified Event \"@{}\" of bean \"{}\"", new Object[] {
						        ann.annotationType().getSimpleName(), bean.getClass().getName() });

						ReflectionUtils.makeAccessible(field);
						//field.setAccessible(true);
						// TODO If there is already object for given annotation, we should re-use it here
						ReflectionUtils.setField(field, bean, new QualifiedEvent(eventRegistry, ann));
						//field.set(bean, new QualifiedEvent(eventRegistry, ann));
					}
				}
			}
		}, ReflectionUtils.COPYABLE_FIELDS);

		ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				int observesAnnotationsFound = 0;
				Annotation qualifierAnnotation = null;
				TransactionPhase phase = null;
				Class<?> annotatedParameterType = null;
				Class<?>[] parameterTypes = method.getParameterTypes();
				for (int i = 0; i < parameterTypes.length; i++) {
					Annotation[] paramAnnotations = new MethodParameter(method, i).getParameterAnnotations();
					for (int c = 0; c < paramAnnotations.length; c++) {
						if (observesAnnotationsFound == 1) {
							if (paramAnnotations[c].annotationType().isAnnotationPresent(Qualifier.class)) {
								qualifierAnnotation = paramAnnotations[c];
							}
							break;
						}
						if (paramAnnotations[c].annotationType().equals(Observes.class)) {
							observesAnnotationsFound++;
							annotatedParameterType = parameterTypes[i];
							phase = ((Observes) paramAnnotations[c]).during();
						}
					}
				}
				if (observesAnnotationsFound > 1) {
					throw new IllegalStateException(
					        "Observer method must have exactly one annotated parameter, but found "
					                + observesAnnotationsFound);
				}

				if (observesAnnotationsFound == 1) {
					if (method.isVarArgs()) {
						throw new IllegalStateException(
						        "Observer method was declared to take a variable number of arguments");
					}
					if (!method.getReturnType().getName().equals("void")) {
						throw new IllegalStateException("Observer method " + method + " must return void");
					}
					if (parameterTypes.length > 1) {
						throw new IllegalStateException("Observer method must have exactly one parameter, but found "
						        + parameterTypes.length);
					}
					eventRegistry.registerEvent(bean, method, annotatedParameterType, phase, qualifierAnnotation);
				}
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}
}
