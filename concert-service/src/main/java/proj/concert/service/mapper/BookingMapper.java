package proj.concert.service.mapper;
import proj.concert.common.dto.BookingDTO;
import proj.concert.common.dto.SeatDTO;
import proj.concert.service.domain.Booking;
import proj.concert.service.domain.Seat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingMapper {
    public static BookingDTO toDto(Booking booking) {
        List<SeatDTO> dtoSeats = new ArrayList<SeatDTO>();
        for (Seat seat : booking.getSeats()){
            dtoSeats.add(SeatMapper.toDto(seat));
        }
        return new BookingDTO(booking.getConcertId(), booking.getDate(), dtoSeats);
    }

    public static List<BookingDTO> toDtoList(List<Booking> bookings) {
        List<BookingDTO> dtoBookings = new ArrayList<>();
        for (Booking booking : bookings){
            dtoBookings.add(toDto(booking));
        }
        return dtoBookings;
    }
}