package ae.mashreq.conference.booker.service;

import ae.mashreq.conference.booker.controller.dto.AvailableRoomDto;
import ae.mashreq.conference.booker.controller.dto.AvailableRoomsResponseDto;
import ae.mashreq.conference.booker.controller.dto.BookingRequestDto;
import ae.mashreq.conference.booker.controller.dto.BookingResponseDto;
import ae.mashreq.conference.booker.model.Room;
import ae.mashreq.conference.booker.repository.BookingRepository;
import ae.mashreq.conference.booker.repository.entity.BookingEntity;
import ae.mashreq.conference.booker.service.mapper.BookingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Spy
    @InjectMocks
    private BookingService bookingService;

    @Nested
    class Booking {

        private final BookingRequestDto request = BookingRequestDto.builder()
                .startTime("start time")
                .endTime("end time")
                .numberOfPeople(10)
                .build();
        private final Long startTime = 1L;
        private final Long endTime = 2L;

        @BeforeEach
        void setUp() {
            doReturn(startTime).when(bookingService).toEpochSeconds(request.getStartTime());
            doReturn(endTime).when(bookingService).toEpochSeconds(request.getEndTime());
        }

        @DisplayName("should successfully create booking")
        @Test
        void shouldSuccessfullyCreateBooking() {
            // given
            final ArgumentCaptor<BookingEntity> entityArgumentCaptor = ArgumentCaptor.forClass(BookingEntity.class);
            final BookingEntity savedEntity = Mockito.mock(BookingEntity.class);
            final BookingResponseDto expectedResponse = Mockito.mock(BookingResponseDto.class);
            when(bookingService.chooseRoom(startTime, endTime, request.getNumberOfPeople())).thenReturn(Optional.of(Room.AMAZE.getName()));
            when(bookingRepository.save(entityArgumentCaptor.capture())).thenReturn(savedEntity);
            when(bookingMapper.toSuccessfulBookingResponse(savedEntity, request)).thenReturn(expectedResponse);
            // when
            final BookingResponseDto result = bookingService.book(request);
            // then
            assertThat(result).isEqualTo(expectedResponse);
            verify(bookingRepository).save(any());
            assertThat(entityArgumentCaptor.getValue().getStartTime()).isEqualTo(startTime);
            assertThat(entityArgumentCaptor.getValue().getEndTime()).isEqualTo(endTime);
            assertThat(entityArgumentCaptor.getValue().getRoomName()).isEqualTo(Room.AMAZE.getName());
            assertThat(entityArgumentCaptor.getValue().getNumberOfPeople()).isEqualTo(request.getNumberOfPeople());
            verify(bookingMapper).toSuccessfulBookingResponse(savedEntity, request);
        }

        @DisplayName("should fail - no rooms available")
        @Test
        void shouldFailToCreateBooking() {
            // given
            final BookingResponseDto expectedResponse = Mockito.mock(BookingResponseDto.class);
            when(bookingService.chooseRoom(startTime, endTime, request.getNumberOfPeople())).thenReturn(Optional.empty());
            when(bookingMapper.toNotFoundBookingResponse(request)).thenReturn(expectedResponse);
            // when
            final BookingResponseDto result = bookingService.book(request);
            // then
            assertThat(result).isEqualTo(expectedResponse);
            verify(bookingRepository, times(0)).save(any());
            verify(bookingMapper).toNotFoundBookingResponse(request);
        }
    }

    @Nested
    class ChooseRoom {
        private final Long startTime = 1L;
        private final Long endTime = 2L;

        @DisplayName("should return room")
        @Test
        void shouldReturnRoom() {
            // given
            when(bookingService.isRoomAvailableForGivenTime(startTime, endTime, Room.AMAZE)).thenReturn(true);
            // when
            final Optional<String> result = bookingService.chooseRoom(startTime, endTime, 3);
            // then
            assertThat(result).isEqualTo(Optional.of(Room.AMAZE.getName()));
        }

        @DisplayName("should return room - for enough capacity")
        @Test
        void shouldReturnRoomForCapacity() {
            // given
            doReturn(true).when(bookingService).isRoomAvailableForGivenTime(any(), any(), any());
            // when
            final Optional<String> result = bookingService.chooseRoom(startTime, endTime, 12);
            // then
            assertThat(result).isEqualTo(Optional.of(Room.INSPIRE.getName()));
        }

        @DisplayName("should return empty room - too large capacity")
        @Test
        void shouldReturnEmptyRoomTooLargeCapacity() {
            // given
            // when
            final Optional<String> result = bookingService.chooseRoom(startTime, endTime, 100);
            // then
            assertThat(result).isEqualTo(Optional.empty());
        }

        @DisplayName("should return empty room - no rooms available")
        @Test
        void shouldReturnEmptyRoomNoRoomsAvailable() {
            // given
            doReturn(false).when(bookingService).isRoomAvailableForGivenTime(any(), any(), any());
            // when
            final Optional<String> result = bookingService.chooseRoom(startTime, endTime, 5);
            // then
            assertThat(result).isEqualTo(Optional.empty());
        }
    }

    @Nested
    class IsRoomAvailable {
        private final Long startTime = 1L;
        private final Long endTime = 2L;
        private final Room room = Room.AMAZE;


        @DisplayName("should return true")
        @Test
        void shouldReturnTrue() {
            // given
            when(bookingRepository.existsByRoomAndStartAndEndTime(room.getName(), startTime, endTime)).thenReturn(false);
            // when
            final boolean result = bookingService.isRoomAvailableForGivenTime(startTime, endTime, room);
            // then
            assertThat(result).isTrue();
        }
        @DisplayName("should return false")
        @Test
        void shouldReturnFalse() {
            // given
            when(bookingRepository.existsByRoomAndStartAndEndTime(room.getName(), startTime, endTime)).thenReturn(true);
            // when
            final boolean result = bookingService.isRoomAvailableForGivenTime(startTime, endTime, room);
            // then
            assertThat(result).isFalse();
        }
    }
    @Nested
    class ToEpochSeconds {

        @DisplayName("should return valid date in epoch seconds")
        @Test
        void shouldReturnValidDateInEpochSeconds() {
            // given
            final String date = "2011-12-03T10:20:00";
            final Long epochSeconds = 1322893200L;
            // when
            final Long result = bookingService.toEpochSeconds(date);
            // then
            assertThat(result).isEqualTo(epochSeconds);
        }
    }
    @Nested
    class GetAvailableRooms {
        private final String startTime = "startTime";
        private final String endTime = "endTime";
        private final Long startTimeSeconds = 1L;
        private final Long endTimeSeconds = 2L;

        @BeforeEach
        void setUp() {
            doReturn(startTimeSeconds).when(bookingService).toEpochSeconds(startTime);
            doReturn(endTimeSeconds).when(bookingService).toEpochSeconds(endTime);
        }

        @DisplayName("should return available rooms - all")
        @Test
        void shouldReturnAvailableRoomsAll() {
            // given
            doReturn(true).when(bookingService).isRoomAvailableForGivenTime(any(), any(), any());
            // when
            final AvailableRoomsResponseDto result = bookingService.getAvailableRooms(startTime, endTime);
            // then
            assertThat(result.getAvailableRooms()).hasSize(4);
        }

        @DisplayName("should return available rooms - only available")
        @Test
        void shouldReturnAvailableRoomsOnlyAvailable() {
            // given
            doReturn(false).when(bookingService).isRoomAvailableForGivenTime(startTimeSeconds, endTimeSeconds, Room.AMAZE);
            doReturn(true).when(bookingService).isRoomAvailableForGivenTime(startTimeSeconds, endTimeSeconds, Room.BEAUTY);
            doReturn(false).when(bookingService).isRoomAvailableForGivenTime(startTimeSeconds, endTimeSeconds, Room.INSPIRE);
            doReturn(false).when(bookingService).isRoomAvailableForGivenTime(startTimeSeconds, endTimeSeconds, Room.STRIVE);
            // when
            final AvailableRoomsResponseDto result = bookingService.getAvailableRooms(startTime, endTime);
            // then
            assertThat(result.getAvailableRooms()).hasSize(1);
            assertThat(result.getAvailableRooms()).extracting(AvailableRoomDto::getRoomName).contains("Beauty");
        }

        @DisplayName("should return empty list")
        @Test
        void shouldReturnEmptyList() {
            // given
            doReturn(false).when(bookingService).isRoomAvailableForGivenTime(any(), any(), any());
            // when
            final AvailableRoomsResponseDto result = bookingService.getAvailableRooms(startTime, endTime);
            // then
            assertThat(result.getAvailableRooms()).isEmpty();
        }
    }
}