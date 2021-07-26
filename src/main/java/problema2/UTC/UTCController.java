package problema2.UTC;



import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import problema2.global.Constantes;
import problema2.global.exception.BadRequestException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UTCController {


    @Autowired
    private UTCServices utcServices;


    @PostMapping("/timezone-utc")
    public ResponseEntity<byte[]> convertirHoraUTC (@RequestBody UTC utctime) throws ParseException {


        if (utctime.getTime() == null)
        {
            throw new BadRequestException(101, "time");
        }
        if (utctime.getTimezone() == null)
        {
            throw new BadRequestException(101, "timezone");
        }

        Boolean valid = utcServices.validate(utctime.getTime());
        if(!valid){
            throw new BadRequestException(121);
        }

    try {
        ZoneId zid = ZoneId.of(Constantes.UTC+utctime.getTimezone());
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        Date d = dateFormat.parse(utctime.getTime());
        ZonedDateTime timeUTC = ZonedDateTime.ofInstant(d.toInstant(),
                zid);
        JsonObject pre = new JsonObject();

        pre.addProperty("time",timeUTC.toLocalTime().format(dtf)); //Mantener formato de hh:mm:ss

        pre.addProperty("timezone",Constantes.UTC+utctime.getTimezone());

        JsonObject preresponse = new JsonObject();
        preresponse.add("response",pre);
        String response = preresponse.toString();
        byte[] responsefile = response.getBytes();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=response.json")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(responsefile.length)
                .body(responsefile);


    }catch (DateTimeException e){
        throw new BadRequestException(120);
    }

    }

}
