package ae.mashreq.conference.booker.controller;

import ae.mashreq.conference.booker.controller.dto.AvailableRoomsResponseDto;
import ae.mashreq.conference.booker.controller.dto.BookingRequestDto;
import ae.mashreq.conference.booker.controller.dto.BookingResponseDto;
import ae.mashreq.conference.booker.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/book")
    ResponseEntity<BookingResponseDto> book(@RequestBody final BookingRequestDto bookingRequest) {
        return ResponseEntity.ok(bookingService.book(bookingRequest));
    }

    @GetMapping
    ResponseEntity<AvailableRoomsResponseDto> getRooms(
            @RequestParam("start_time") final String startTime,
            @RequestParam("end_time") final String endTime
    ) {
        return ResponseEntity.ok(bookingService.getAvailableRooms(startTime, endTime));
    }
}
