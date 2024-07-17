package ae.mashreq.conference.booker.service;

import ae.mashreq.conference.booker.controller.dto.AvailableRoomDto;
import ae.mashreq.conference.booker.controller.dto.AvailableRoomsResponseDto;
import ae.mashreq.conference.booker.controller.dto.BookingRequestDto;
import ae.mashreq.conference.booker.controller.dto.BookingResponseDto;
import ae.mashreq.conference.booker.model.Room;
import ae.mashreq.conference.booker.repository.BookingRepository;
import ae.mashreq.conference.booker.repository.entity.BookingEntity;
import ae.mashreq.conference.booker.service.mapper.BookingMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingResponseDto book(final BookingRequestDto request) {
        final Long startTime = toEpochSeconds(request.getStartTime());
        final Long endTime = toEpochSeconds(request.getEndTime());
        final Optional<String> room = chooseRoom(startTime, endTime, request.getNumberOfPeople());
        if (room.isEmpty()) {
            return bookingMapper.toNotFoundBookingResponse(request);
        }
        final BookingEntity entity = BookingEntity.builder()
                .startTime(startTime)
                .endTime(endTime)
                .roomName(room.get())
                .numberOfPeople(request.getNumberOfPeople())
                .build();
        final BookingEntity savedEntity = bookingRepository.save(entity);
        return bookingMapper.toSuccessfulBookingResponse(savedEntity, request);
    }

    Optional<String> chooseRoom(final Long startTime, final Long endTime, final Integer numberOfPeople) {
        return Arrays.stream(Room.values()).sorted(Comparator.comparing(Room::getCapacity))
                .filter(room -> room.getCapacity() >= numberOfPeople)
                .filter(room -> isRoomAvailableForGivenTime(startTime, endTime, room))
                .findFirst()
                .map(Room::getName);
    }

    boolean isRoomAvailableForGivenTime(final Long startTime, final Long endTime, final Room room) {
        return !bookingRepository.existsByRoomAndStartAndEndTime(room.getName(), startTime, endTime);
    }

    Long toEpochSeconds(final String date) {
        return Instant.from(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()).parse(date)).getEpochSecond();
    }

    public AvailableRoomsResponseDto getAvailableRooms(final String startTime, final String endTime) {
        final Long startTimeSeconds = toEpochSeconds(startTime);
        final Long endTimeSeconds = toEpochSeconds(endTime);
        final List<AvailableRoomDto> rooms = Arrays.stream(Room.values())
                .filter(room -> isRoomAvailableForGivenTime(startTimeSeconds, endTimeSeconds, room))
                .map(room -> AvailableRoomDto.builder()
                            .roomName(room.getName())
                            .maxCapacity(room.getCapacity())
                            .build())
                .toList();
        return AvailableRoomsResponseDto.builder().availableRooms(rooms).build();
    }
}
