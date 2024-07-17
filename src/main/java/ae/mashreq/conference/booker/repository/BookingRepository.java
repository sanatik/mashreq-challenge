package ae.mashreq.conference.booker.repository;

import ae.mashreq.conference.booker.repository.entity.BookingEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface BookingRepository extends CrudRepository<BookingEntity, UUID> {

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM BOOKINGS WHERE ROOM_NAME = :roomName AND " +
    "((START_TIME < :endTime AND END_TIME > :startTime) OR " +
    "(START_TIME >= :startTime AND START_TIME < :endTime) OR " +
    "(END_TIME > :startTime AND END_TIME <= :endTime))")
    boolean existsByRoomAndStartAndEndTime(
            final String roomName, final Long startTime, final Long endTime
    );
}
