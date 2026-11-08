package com.example.batch.log;

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
@Table(name = "log_entry")
@Getter
@Setter
@NoArgsConstructor
public class LogEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "exception_message", length = 4000)
	private String exceptionMessage;

	public LogEntry(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

}
