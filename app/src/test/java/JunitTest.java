import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by thiago on 6/21/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class JunitTest {

    private ChessController chessController;

    @Before
    public void init() {
        chessController = new ChessController();
    }

    @Test
    public void test() throws Exception {
        chessController.iniciaJogo();
        chessController.fimDeJogo();


    }
}
