package ch.wisv.chue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebControllerTest {
    @Mock
    private HueService hueService;

    @InjectMocks
    private WebController webController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testColorHexAll() {
        String response = webController.color("all", "ff0000");

        assertThat(response, is("Changed colour of lamps (all) to #ff0000"));
    }

    @Test
    public void testColorHexID() {
        String response = webController.color("1", "0000ff");

        assertThat(response, is("Changed colour of lamps (1) to #0000ff"));
    }

    @Test
    public void testColorFriendlyAll() {
        String response = webController.colorFriendly("all", "red");

        assertThat(response, is("Changed colour of lamps (all) to #ff0000"));
    }

    @Test
    public void testColorFriendlyID() {
        String response = webController.colorFriendly("2", "blue");

        assertThat(response, is("Changed colour of lamps (2) to #0000ff"));
    }
}
