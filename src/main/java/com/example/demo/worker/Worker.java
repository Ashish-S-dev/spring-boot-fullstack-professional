package com.example.demo.worker;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "workers", indexes = { @Index(name = "idx_worker_phone", columnList = "phone"),
		@Index(name = "idx_worker_active", columnList = "active") }, uniqueConstraints = {
				@UniqueConstraint(name = "uk_worker_phone", columnNames = "phone") })
public class Worker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 100)
	private String name;

	@NotBlank
	@Column(nullable = false, unique = true, length = 15)
	private String phone;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Designation designation;

	@NotNull
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal dailyWageRate;

	@Column(nullable = false)
	private boolean active = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Designation getDesignation() {
		return designation;
	}

	public void setDesignation(Designation designation) {
		this.designation = designation;
	}

	public BigDecimal getDailyWageRate() {
		return dailyWageRate;
	}

	public void setDailyWageRate(BigDecimal dailyWageRate) {
		this.dailyWageRate = dailyWageRate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Worker [id=" + id + ", name=" + name + ", phone=" + phone + ", designation=" + designation
				+ ", dailyWageRate=" + dailyWageRate + ", active=" + active + "]";
	}

	public Worker(@NotBlank String name, @NotBlank String phone, @NotNull Designation designation,
			@NotNull BigDecimal dailyWageRate, boolean active) {
		super();
		this.name = name;
		this.phone = phone;
		this.designation = designation;
		this.dailyWageRate = dailyWageRate;
		this.active = active;
	}
	
	public Worker() {}
	
	
}