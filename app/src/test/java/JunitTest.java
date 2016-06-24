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

    @Mock
    MyPocketChess main;


    @Test
    public void test() throws Exception {

        Mockito.when(main.timeLimit()).thenReturn(1);
        Mockito.when(main.randomMode()).thenReturn(false);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable)invocation.getArguments()[0]).run();
                return null;
            }
        }).when(main).runOnUIThread(Mockito.any(Runnable.class));

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

        Move m = new Move(1, 16, 0);
        engineComponent.humanMove(m);
        System.out.println(engineComponent.getFEN());
        while (!engineComponent.humansTurn()){
            engineComponent.startComputerMove();
        }
        System.out.println(engineComponent.getFEN());
        m = new Move(9, 17, 0);
        engineComponent.humanMove(m);
        System.out.println(engineComponent.getFEN());
        while (!engineComponent.humansTurn()){
            engineComponent.startComputerMove();
        }
        System.out.println(engineComponent.getFEN());

    }
}
