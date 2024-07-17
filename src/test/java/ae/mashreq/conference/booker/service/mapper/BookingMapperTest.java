package ae.mashreq.conference.booker.service.mapper;

import ae.mashreq.conference.booker.controller.dto.BookingRequestDto;
import ae.mashreq.conference.booker.controller.dto.BookingResponseDto;
import ae.mashreq.conference.booker.model.BookingStatus;
import ae.mashreq.conference.booker.repository.entity.BookingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    private final BookingRequestDto request = BookingRequestDto.builder()
            .startTime("start time")
            .endTime("end time")
            .numberOfPeople(10)
            .build();

    @DisplayName("should map to successful booking response")
    @Test
    void toSuccessfulBookingResponse() {
        final BookingEntity entity = BookingEntity.builder()
                .id(UUID.randomUUID())
                .roomName("room name")
                .numberOfPeople(request.getNumberOfPeople())
                .build();
        final BookingResponseDto result = bookingMapper.toSuccessfulBookingResponse(entity, request);
        assertThat(result.getBookingId()).isEqualTo(entity.getId());
        assertThat(result.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(result.getEndTime()).isEqualTo(request.getEndTime());
        assertThat(result.getNumberOfPeople()).isEqualTo(request.getNumberOfPeople());
        assertThat(result.getRoomName()).isEqualTo(entity.getRoomName());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.SUCCESS.name());
    }

    @DisplayName("should map to no room found booking response")
    @Test
    void toNotFoundBookingResponse() {
        final BookingResponseDto result = bookingMapper.toNotFoundBookingResponse(request);
        assertThat(result.getBookingId()).isNull();
        assertThat(result.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(result.getEndTime()).isEqualTo(request.getEndTime());
        assertThat(result.getNumberOfPeople()).isEqualTo(request.getNumberOfPeople());
        assertThat(result.getRoomName()).isNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.NO_ROOMS_AVAILABLE.name());
    }
}