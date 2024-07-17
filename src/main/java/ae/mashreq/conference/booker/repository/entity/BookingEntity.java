package ae.mashreq.conference.booker.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("BOOKINGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {
    @Id
    @Column("ID")
    private UUID id;
    @Column("START_TIME")
    private Long startTime;
    @Column("END_TIME")
    private Long endTime;
    @Column("ROOM_NAME")
    private String roomName;
    @Column("NUMBER_OF_PEOPLE")
    private Integer numberOfPeople;
}
