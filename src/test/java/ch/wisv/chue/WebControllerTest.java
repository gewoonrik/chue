package ch.wisv.chue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WebControllerTest {
    @Mock
    private HueService hueService;

    @InjectMocks
    private WebController webController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    public void testAlert() throws Exception {
        mockMvc.perform(get("/alert"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Alerting for 5000 milliseconds")));
    }

    @Test
    public void testAlertTimeOut() throws Exception {
        mockMvc.perform(get("/alert")
                .param("timeout", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Alerting for 100 milliseconds")));
    }

    @Test
    public void testOranje() throws Exception {
        mockMvc.perform(get("/oranje"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("B'voranje")));
    }

    @Test
    public void testColorHexAll() throws Exception {
        mockMvc.perform(get("/color/all/ff0000"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Changed colour of lamps (all) to #ff0000")));
    }

    @Test
    public void testColorHexID() throws Exception {
        mockMvc.perform(get("/color/1/0000ff"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Changed colour of lamps (1) to #0000ff")));
    }

    @Test
    public void testColorFriendlyAll() throws Exception {
        mockMvc.perform(get("/color/all/red"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Changed colour of lamps (all) to #ff0000")));
    }

    @Test
    public void testColorFriendlyID() throws Exception {
        mockMvc.perform(get("/color/2/blue"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Changed colour of lamps (2) to #0000ff")));
    }

    @Test
    public void testColorPost() throws Exception {
        mockMvc.perform((post("/color"))
                .param("id[]", "1,2")
                .param("hex", "#cc0000"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Changed colour of lamps ([1, 2]) to #cc0000")));
    }
}
