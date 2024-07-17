package ae.mashreq.conference.booker.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostBookingIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @DisplayName("should successfully book a room")
    @Test
    public void shouldSuccessfullyBookARoom() throws Exception {
        final String requestBody = """
                {
                "startTime": "2011-12-03T10:15:00",
                "endTime": "2011-12-03T10:30:00",
                "numberOfPeople": 5
                }
                """;
        mvc.perform(post("/rooms/book").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.roomName").value("Beauty"))
                .andExpect(jsonPath("$.startTime").value("2011-12-03T10:15:00"))
                .andExpect(jsonPath("$.endTime").value("2011-12-03T10:30:00"))
                .andExpect(jsonPath("$.numberOfPeople").value(5))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @DisplayName("should fail to book a room - no rooms available")
    @Test
    public void shouldFailToBookARoom() throws Exception {

        final String requestBody = """
                {
                "startTime": "2011-12-03T10:15:00",
                "endTime": "2011-12-03T10:30:00",
                "numberOfPeople": 20
                }
                """;
        mvc.perform(post("/rooms/book").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.roomName").value("Strive"))
                .andExpect(jsonPath("$.startTime").value("2011-12-03T10:15:00"))
                .andExpect(jsonPath("$.endTime").value("2011-12-03T10:30:00"))
                .andExpect(jsonPath("$.numberOfPeople").value(20))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mvc.perform(post("/rooms/book").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").isEmpty())
                .andExpect(jsonPath("$.roomName").isEmpty())
                .andExpect(jsonPath("$.startTime").value("2011-12-03T10:15:00"))
                .andExpect(jsonPath("$.endTime").value("2011-12-03T10:30:00"))
                .andExpect(jsonPath("$.numberOfPeople").value(20))
                .andExpect(jsonPath("$.status").value("NO_ROOMS_AVAILABLE"));
    }
}
