import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Mock
    MyPocketChess main;


    @Test
    public void test() throws Exception {
        IManager engineManager = ComponentFactory.createInstance();
        engineManager.setRequiredInterface("IGUIInterface", main);
        br.unicamp.ic.sed.historymgr.prov.IManager historyManager = br.unicamp.ic.sed.historymgr.impl.ComponentFactory.createInstance();
        br.unicamp.ic.sed.engine_history_connector.impl.IManager engineHistoryConnectorManager = br.unicamp.ic.sed.engine_history_connector.impl.ComponentFactory.createInstance();
        engineHistoryConnectorManager.setRequiredInterface("IHistory", historyManager.getProvidedInterface("IHistory"));
        engineManager.setRequiredInterface("IHistory", engineHistoryConnectorManager.getProvidedInterface("IHistory"));
        IEngine engineComponent = (IEngine) engineManager.getProvidedInterface("IEngine");
        engineComponent.setThreadStackSize(32768);


        boolean playerWhite = true;
        int ttLogSize = 16;
        engineComponent.newGame(playerWhite, ttLogSize, false);
        engineComponent.startGame();

        System.out.println(engineComponent.getFEN());
        Move m = new Move(1, 16, 0);
        engineComponent.makeHumanHumanMove(m);
        System.out.println(engineComponent.getFEN());

    }
}
