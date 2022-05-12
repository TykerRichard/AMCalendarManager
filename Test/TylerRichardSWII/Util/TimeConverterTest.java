package TylerRichardSWII.Util;

import TylerRichardSWII.AllClass.Main;
import TylerRichardSWII.Util.TimeConverter;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TimeConverterTest {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private LocalDateTime timeDefault;
    JFXPanel fxPanel = new JFXPanel();


    @Test
    void ConvertToUTCTestWithArguments() throws Exception{

        TimeConverter timeConverter=new TimeConverter();
        assertEquals(ZonedDateTime.now(Clock.systemUTC()).withZoneSameInstant(ZoneId.of(("UTC"))
        ), timeConverter.convertToUTC(LocalDate.now(),LocalTime.now()));
    }
}