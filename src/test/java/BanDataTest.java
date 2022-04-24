import me.metropanties.TMPChecker.ban.BanChecker;
import me.metropanties.TMPChecker.ban.BanData;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BanDataTest {

    private final BanChecker banChecker = new BanChecker();

    @Test
    public void retrieveBanData() {
        Optional<BanData> banData = banChecker.getBanData(76561198190169177L);
        assertTrue(banData.isPresent());
    }

}
