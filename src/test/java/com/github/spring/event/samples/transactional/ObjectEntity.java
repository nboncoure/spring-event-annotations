package com.github.spring.event.samples.transactional;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class ObjectEntity.
 */
@Entity
@Table(name = "objectEntity")
public class ObjectEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	Long id;

	/** The value. */
	@Column(nullable = false)
	String value;

	/**
	 * Instantiates a new object entity.
	 */
	public ObjectEntity() {

	}

	/**
	 * Instantiates a new object entity.
	 * 
	 * @param id
	 *            the id
	 * @param value
	 *            the value
	 */
	public ObjectEntity(final Long id, final String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(final String value) {
		this.value = value;
	}
}
