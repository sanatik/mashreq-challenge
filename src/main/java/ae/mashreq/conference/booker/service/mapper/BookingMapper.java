package ae.mashreq.conference.booker.service.mapper;

import ae.mashreq.conference.booker.controller.dto.BookingRequestDto;
import ae.mashreq.conference.booker.controller.dto.BookingResponseDto;
import ae.mashreq.conference.booker.model.BookingStatus;
import ae.mashreq.conference.booker.repository.entity.BookingEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BookingMapper {

    public BookingResponseDto toSuccessfulBookingResponse(final BookingEntity bookingEntity, final BookingRequestDto request) {
        return BookingResponseDto.builder()
                .bookingId(bookingEntity.getId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .numberOfPeople(bookingEntity.getNumberOfPeople())
                .roomName(bookingEntity.getRoomName())
                .status(BookingStatus.SUCCESS.name())
                .build();
    }

    public BookingResponseDto toNotFoundBookingResponse(final BookingRequestDto request) {
        return BookingResponseDto.builder()
                .bookingId(null)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .numberOfPeople(request.getNumberOfPeople())
                .roomName(null)
                .status(BookingStatus.NO_ROOMS_AVAILABLE.name())
                .build();
    }
}
