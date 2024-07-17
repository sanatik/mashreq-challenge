package ae.mashreq.conference.booker.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class AvailableRoomsResponseDto {
    private List<AvailableRoomDto> availableRooms;
}
