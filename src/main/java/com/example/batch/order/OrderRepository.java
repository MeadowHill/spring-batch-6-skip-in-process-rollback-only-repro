package com.example.batch.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("select o.id from Order o order by o.id")
	List<Long> findAllIds();

}
