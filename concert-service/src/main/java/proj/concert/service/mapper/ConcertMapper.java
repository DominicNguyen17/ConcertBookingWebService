package proj.concert.service.mapper;

import proj.concert.common.dto.ConcertDTO;
import proj.concert.common.dto.ConcertSummaryDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConcertMapper {
    public static Concert toDomainModel(ConcertDTO dtoConcert) {
        Concert fullConcert = new Concert(dtoConcert.getId(),
                dtoConcert.getTitle(),
                dtoConcert.getImageName(),
                dtoConcert.getBlurb());

        fullConcert.getDates().addAll(dtoConcert.getDates());
        Set<Performer> performers = new HashSet<>();
        for (PerformerDTO performer: dtoConcert.getPerformers()) performers.add(PerformerMapper.toDomainModel(performer));
        fullConcert.setPerformers(performers);
        return fullConcert;
    }

    public static ConcertDTO toDto(Concert concert) {
        ConcertDTO dtoConcert = new ConcertDTO(concert.getId(),
                concert.getTitle(),
                concert.getImageName(),
                concert.getBlrb());

        dtoConcert.getDates().addAll(concert.getDates());
        List<PerformerDTO> dtoPerformers = new ArrayList<>();
        for (Performer performer: concert.getPerformers()) dtoPerformers.add(PerformerMapper.toDto(performer));
        dtoConcert.setPerformers(dtoPerformers);
        return dtoConcert;
    }

    public static ConcertSummaryDTO toSummaryDto(Concert concert) {
        ConcertSummaryDTO summary = new ConcertSummaryDTO(concert.getId(),
                concert.getTitle(), concert.getImageName());
        return summary;
    }
}
