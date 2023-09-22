package proj.concert.service.mapper;


import proj.concert.common.dto.SeatDTO;
import proj.concert.service.domain.Seat;

import java.time.LocalDateTime;

public class SeatMapper {
    public static SeatDTO toDto(Seat seat) {
        return new SeatDTO(seat.getLabel(), seat.getCost());
    }
}