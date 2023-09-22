package proj.concert.service.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import proj.concert.common.jackson.LocalDateTimeDeserializer;
import proj.concert.common.jackson.LocalDateTimeSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Seat{

    // TODO Implement this class.
	@Id
	@GeneratedValue
	private Long id;
	private String label;
	private boolean isBooked;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime date;
	private BigDecimal cost;
	private Long bookingId;
	private Long concertId;
	@Version
	private long version;

	public Seat(Long id, String label, boolean isBooked, LocalDateTime date, BigDecimal cost) {
		this.id = id;
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.cost = cost;
	}
	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal cost) {
		this(null, label, isBooked, date, cost);
	}	
	
	public Seat() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() { return label; }

	public void setLabel(String label) { this.label = label; }

	public boolean getBooked() { return isBooked; }

	public void setBooked(boolean isBooked) { this.isBooked = isBooked; }

	public LocalDateTime getDate() { return date; }

	public void setDate(LocalDateTime date) { this.date = date; }

	public BigDecimal getCost() { return cost; }

	public void setCost(BigDecimal cost) { this.cost = cost; }

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Long getConcertId() {
		return concertId;
	}

	public void setConcertId(Long concertId) {
		this.concertId = concertId;
	}

	public long getVersion() { return version; }
	public void setVersion(long version) { this.version = version; }
}
