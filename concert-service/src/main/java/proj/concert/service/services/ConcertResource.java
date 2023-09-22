package proj.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proj.concert.common.dto.*;
import proj.concert.common.types.BookingStatus;
import proj.concert.service.domain.*;
import proj.concert.service.jaxrs.LocalDateTimeParam;
import proj.concert.service.mapper.*;
import proj.concert.service.util.TheatreLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
import java.time.LocalDateTime;
import java.util.*;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    // TODO Implement this.
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);
    private static final Map<AsyncResponse, ConcertInfoSubscriptionDTO> subs = new HashMap<>();

    @GET
    @Path("/concerts/{id}")
    public ConcertDTO getSingleConcert(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            Concert concert = em.find(Concert.class, id);
            em.getTransaction().commit();

            if (concert == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
            return ConcertMapper.toDto(concert);
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/concerts")
    public List<ConcertDTO> getAllConcerts() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Concert> query = em.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = query.getResultList();
            List<ConcertDTO> dtoConcerts = new ArrayList<>();
            for (Concert concert: concerts) dtoConcerts.add(ConcertMapper.toDto(concert));
            em.getTransaction().commit();
            if (concerts.size() == 0) throw new WebApplicationException(Response.Status.NOT_FOUND);
            return dtoConcerts;
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/concerts/summaries")
    public List<ConcertSummaryDTO> getConcertSummaries() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Concert> query = em.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = query.getResultList();
            List<ConcertSummaryDTO> summaries = new ArrayList<>();
            for (Concert concert: concerts) summaries.add(ConcertMapper.toSummaryDto(concert));
            em.getTransaction().commit();
            if (concerts.size() == 0) throw new WebApplicationException(Response.Status.NOT_FOUND);
            return summaries;
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/performers/{id}")
    public PerformerDTO getSinglePerformer(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            Performer performer = em.find(Performer.class, id);
            em.getTransaction().commit();

            if (performer == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
            return PerformerMapper.toDto(performer);
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/performers")
    public List<PerformerDTO> getAllPerformers() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Performer> query = em.createQuery("select p from Performer p", Performer.class);
            List<Performer> performers = query.getResultList();
            List<PerformerDTO> dtoPerformer = new ArrayList<>();
            for (Performer performer: performers) dtoPerformer.add(PerformerMapper.toDto(performer));
            em.getTransaction().commit();
            if (performers.size() == 0) throw new WebApplicationException(Response.Status.NOT_FOUND);
            return dtoPerformer;
        } finally {
            em.close();
        }
    }

    @POST
    @Path("/login")
    public Response userLogin(UserDTO client) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<User> query = em.createQuery(
                    "select u from User u where u.username = :username and u.password = :password",
                    User.class);
            query.setParameter("username", client.getUsername());
            query.setParameter("password", client.getPassword());
            User user = query.getSingleResult();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            em.getTransaction().commit();
            NewCookie cookie = new NewCookie("auth", token);
            return Response.ok().cookie(cookie).build();
        } catch(NoResultException e) {
            em.getTransaction().commit();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/bookings")
    public Response getBookings(@CookieParam("auth") Cookie token){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            if (token == null) throw new WebApplicationException(Response.Status.UNAUTHORIZED);

            User user = getUserByCookie(token, em);
            if (user == null){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            List<Booking> bookings = em.createQuery("select b from Booking b where b.user.id =: user_id", Booking.class)
                    .setParameter("user_id", user.getId()).getResultList();
            em.getTransaction().commit();
            return Response.ok(BookingMapper.toDtoList(bookings)).build();
        } finally {
            em.close();
        }
    }

    @POST
    @Path("/bookings")
    public Response makeBooking(BookingRequestDTO bookingRequestDTO, @CookieParam("auth") Cookie token, @Context UriInfo uriInfo){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            if (token == null) throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            User user = getUserByCookie(token, em);
//            em.lock(user, LockModeType.OPTIMISTIC);
            Concert concert = em.find(Concert.class, bookingRequestDTO.getConcertId());
            if (concert == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            Set<LocalDateTime> dates = concert.getDates();
            LocalDateTime date = bookingRequestDTO.getDate();
            if (!(dates.contains(date))) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            TypedQuery<Seat> query = em.createQuery("select s from Seat s where s.label =:label and s.date =:date", Seat.class);
            List<Seat> seats = new ArrayList<>();
            for (String seatLabel : bookingRequestDTO.getSeatLabels()){
                query.setParameter("label", seatLabel);
                query.setParameter("date", date);
                Seat seat = query.getSingleResult();
                if (!seat.getBooked()){
                    seat.setBooked(true);
                    seats.add(seat);
                }else{
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                }
            }
            Booking booking = new Booking(bookingRequestDTO.getConcertId(), bookingRequestDTO.getDate(), seats, user);
            em.persist(booking);
            for (Seat seat : seats){
                seat.setBookingId(booking.getId());
                seat.setConcertId(bookingRequestDTO.getConcertId());
                em.persist(seat);
            }
            em.getTransaction().commit();
            sendMessage(em, concert.getId(), bookingRequestDTO.getDate());
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(Long.toString(booking.getId()));
            return Response.created(uriBuilder.build()).build();
        } finally {
            em.close();
        }
    }

    private static User getUserByCookie(Cookie token, EntityManager em) {
        User user = em.createQuery(
                "select u from User u where u.token =: token",
                User.class).setParameter("token", token.getValue()).getSingleResult();
        return user;
    }

    @GET
    @Path("/bookings/{id}")
    public BookingDTO getBookingById(@PathParam("id") Long id, @CookieParam("auth") Cookie token){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, id);
            em.getTransaction().commit();

            if (booking == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
            User user = getUserByCookie(token, em);
            if (!booking.getUser().getId().equals(user.getId())){
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }
            return BookingMapper.toDto(booking);
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/seats/{dateTime}")
    public List<SeatDTO> getBookedSeats(@PathParam("dateTime") LocalDateTimeParam dateTime, @QueryParam("status") String status) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        LocalDateTime localDateTime = dateTime.getLocalDateTime();
        BookingStatus bookingStatus = BookingStatus.valueOf(status);
        try {
            em.getTransaction().begin();
            TypedQuery<Seat> query = em.createQuery("select s from Seat s where s.date =:date", Seat.class);
            query.setParameter("date", localDateTime);
            List<Seat> seats = query.getResultList();
            List<SeatDTO> dtoSeats = new ArrayList<>();
            if (bookingStatus == BookingStatus.Any){
                for (Seat seat : seats) {
                    dtoSeats.add(SeatMapper.toDto(seat));
                }
            }
            if (bookingStatus == BookingStatus.Booked){
                for (Seat seat : seats) {
                    if (seat.getBooked()){
                        dtoSeats.add(SeatMapper.toDto(seat));
                    }
                }
            }
            if (bookingStatus == BookingStatus.Unbooked){
                for (Seat seat : seats) {
                    if (!seat.getBooked()){
                        dtoSeats.add(SeatMapper.toDto(seat));
                    }
                }
            }
            em.getTransaction().commit();
            return dtoSeats;
        } finally {
            em.close();
        }
    }

    @POST
    @Path("/subscribe/concertInfo/")
    public void subscribeConcertInfo(@Suspended AsyncResponse sub, ConcertInfoSubscriptionDTO concertInfoSubscriptionDTO, @CookieParam("auth") Cookie token){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            if (token == null){
                sub.resume(Response.status(Response.Status.UNAUTHORIZED).build());
            }
            Concert concert = em.find(Concert.class, concertInfoSubscriptionDTO.getConcertId());
            if (concert == null) {
                sub.resume(Response.status(Response.Status.BAD_REQUEST).build());
            }
            Set<LocalDateTime> dates = concert.getDates();
            LocalDateTime date = concertInfoSubscriptionDTO.getDate();
            if (!(dates.contains(date))) {
                sub.resume(Response.status(Response.Status.BAD_REQUEST).build());
            }
            em.getTransaction().commit();
            subs.put(sub, concertInfoSubscriptionDTO);
        } finally {
            em.close();
        }
    }

    private void sendMessage(EntityManager em, Long concertId, LocalDateTime localDateTime) {
        TypedQuery<Concert> query = em.createQuery("select c from Concert c join c.dates d where d = :date and c.id = :concertId", Concert.class);
        query.setParameter("date", localDateTime);
        query.setParameter("concertId", concertId);
        List<Concert> concerts = query.getResultList();
        List<Long> concertIds = new ArrayList<>();
        for (Concert concert : concerts){
            concertIds.add(concert.getId());
        }
        List<AsyncResponse> remove = new ArrayList<>();
        for (Map.Entry<AsyncResponse, ConcertInfoSubscriptionDTO> e : subs.entrySet()) {
            if(e.getValue().getConcertId() == concertIds.get(0)){
                List<Seat> seats = em.createQuery("select s from Seat s where s.date = :date and s.isBooked = true", Seat.class)
                        .setParameter("date", localDateTime)
                        .getResultList();
                int size = seats.size();
                double currentPercentageBooked = ((double) size / TheatreLayout.NUM_SEATS_IN_THEATRE) * 100;
                if ((int) currentPercentageBooked > e.getValue().getPercentageBooked()) {
                    int remainingSeats = TheatreLayout.NUM_SEATS_IN_THEATRE - size;
                    ConcertInfoNotificationDTO infoNotificationDTO = new ConcertInfoNotificationDTO(remainingSeats);
                    e.getKey().resume(Response.ok(infoNotificationDTO).build());
                    remove.add(e.getKey());
                }
            }
        }
        for(AsyncResponse asyncResponse : remove){
            subs.remove(asyncResponse);
        }
    }

    @GET
    @Path("/seats/1/{dateTime}")
    public List<Seat> getSeats(@PathParam("dateTime") LocalDateTimeParam dateTime, @QueryParam("status") String status) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        LocalDateTime localDateTime = dateTime.getLocalDateTime();
        BookingStatus bookingStatus = BookingStatus.valueOf(status);
        try {
            em.getTransaction().begin();
            TypedQuery<Seat> query = em.createQuery("select s from Seat s where s.date =:date", Seat.class);
            query.setParameter("date", localDateTime);
            List<Seat> seats = query.getResultList();
            List<Seat> dtoSeats = new ArrayList<>();
            if (bookingStatus == BookingStatus.Any){
                for (Seat seat : seats) {
                    dtoSeats.add(seat);
                }
            }
            if (bookingStatus == BookingStatus.Booked){
                for (Seat seat : seats) {
                    if (seat.getBooked()){
                        dtoSeats.add(seat);
                    }
                }
            }
            if (bookingStatus == BookingStatus.Unbooked){
                for (Seat seat : seats) {
                    if (!seat.getBooked()){
                        dtoSeats.add(seat);
                    }
                }
            }
            em.getTransaction().commit();
            return dtoSeats;
        } finally {
            em.close();
        }
    }
}
