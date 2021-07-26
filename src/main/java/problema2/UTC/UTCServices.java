package problema2.UTC;


import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UTCServices {
    private Pattern pattern;
    private Matcher matcher;

    private static final String TIME24HOURS_PATTERN =
            "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";

    public UTCServices(){
        pattern = Pattern.compile(TIME24HOURS_PATTERN);
    }

    public Boolean validate(final String time){

        matcher = pattern.matcher(time);
        return matcher.matches();

    }
}
