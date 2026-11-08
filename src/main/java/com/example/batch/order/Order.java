package com.example.batch.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders") // "order" is a reserved SQL keyword
@Getter
@Setter
@NoArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_number", nullable = false)
	private int orderNumber;

	@Column(name = "was_processed", nullable = false)
	private boolean wasProcessed;

	public Order(int orderNumber, boolean wasProcessed) {
		this.orderNumber = orderNumber;
		this.wasProcessed = wasProcessed;
	}

}
