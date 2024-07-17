package ae.mashreq.conference.booker.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class BookingRequestDto {
    private final String startTime;
    private final String endTime;
    private final Integer numberOfPeople;
}
