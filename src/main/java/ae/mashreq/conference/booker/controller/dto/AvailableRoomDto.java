package ae.mashreq.conference.booker.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class AvailableRoomDto {
    private String roomName;
    private Integer maxCapacity;
}
