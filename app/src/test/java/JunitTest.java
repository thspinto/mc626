import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import br.unicamp.ic.sed.engine.impl.ComponentFactory;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.engine.prov.IManager;
import br.unicamp.ic.sed.engine.req.IHistory;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.gui.pocketchess.MyPocketChess;

import static org.junit.Assert.assertTrue;

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
