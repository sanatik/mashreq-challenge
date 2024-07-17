package ae.mashreq.conference.booker.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Data
@Builder
@Jacksonized
public class BookingResponseDto {
    private final UUID bookingId;
    private final String roomName;
    private final String startTime;
    private final String endTime;
    private final Integer numberOfPeople;
    private final String status;
}
