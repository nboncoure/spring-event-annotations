package com.github.spring.event.samples.transactional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.lambdaj.Lambda;

import com.github.spring.event.EventHandledCallback;
import com.github.spring.event.annotation.ObservesAnotationBeanPostProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/transactionalListenerTestContext.xml" })
public class TransactionalListenerTest {

	/** Logger. */
	static Logger logger = LoggerFactory.getLogger(TransactionalListenerTest.class);

	/** The context. */
	@Autowired
	private ApplicationContext context;

	/** The service. */
	@Autowired
	private ObjectEntityService service;

	/** The event handled callback. */
	@Mock
	private EventHandledCallback eventHandledCallback;

	/**
	 * Verify context.
	 */
	@Before
	public void verifyContext() {
		Assert.assertNotNull(context.getBean(ObservesAnotationBeanPostProcessor.class));
		logger.info("Init the mocks before the test.");
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Received logged in event.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void receivedCreationSuccessEvent() throws InterruptedException {
		service.setEventHandledCallback(eventHandledCallback);

		final ObjectEntity entity = new ObjectEntity(1L, "test");
		service.create(entity);
		logger.info("post created");

		final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
		verify(eventHandledCallback, times(5)).eventHandled(captor.capture());

		Assert.assertTrue(captor.getAllValues().contains("itemCreated"));
		Assert.assertTrue(captor.getAllValues().contains("inProgress"));
		Assert.assertTrue(captor.getAllValues().contains("beforeCompletion"));
		Assert.assertTrue(captor.getAllValues().contains("afterCompletion"));
		Assert.assertTrue(captor.getAllValues().contains("afterSuccess"));
		Assert.assertTrue(Lambda.join(captor.getAllValues()).endsWith(
		        ", beforeCompletion, afterCompletion, afterSuccess"));

		final ObjectEntity result = service.findById(1L);
		Assert.assertNotNull(result);
		Assert.assertEquals(entity.getValue(), result.getValue());
		logger.info("post retrieves");
	}

	/**
	 * Received creation failure event.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test(expected = DataIntegrityViolationException.class)
	public void receivedCreationFailureEvent() throws InterruptedException {
		service.setEventHandledCallback(eventHandledCallback);

		final ObjectEntity entity = new ObjectEntity(1L, null);
		service.create(entity);
		logger.info("post created");

		final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
		verify(eventHandledCallback, times(4)).eventHandled(captor.capture());

		Assert.assertEquals("inProgress, beforeCompletion, afterCompletion, afterFailure",
		        Lambda.join(captor.getAllValues()));
	}
}
