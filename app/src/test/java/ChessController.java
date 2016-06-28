import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.ObjectMethodsGuru;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import br.unicamp.ic.sed.engine.impl.ComponentFactory;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.engine.prov.IManager;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.gui.pocketchess.MyPocketChess;

@RunWith(MockitoJUnitRunner.class)
public class ChessController implements java.lang.Cloneable {

    @Mock
    private MyPocketChess main;

    private IEngine engineComponent;

    private String status;

    private State state;

    public Boolean brancoJoga;

    private int i = 0;

    private static List<Move> jogadasValidas = Arrays.asList(new Move(13, 29, 0), new Move(14, 30, 0),
            new Move(8, 16, 0), new Move(16, 24, 0), new Move(9, 17, 0));

    private int moveIndex;

    public ChessController() {
        state = State.HumanoJoga;
        brancoJoga = true;

        /** Mocks */
        MockitoAnnotations.initMocks(this);

        Mockito.when(main.timeLimit()).thenReturn(100);
        Mockito.when(main.randomMode()).thenReturn(false);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable)invocation.getArguments()[0]).run();
                return null;
            }
        }).when(main).runOnUIThread(Mockito.any(Runnable.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                status = ((String)invocation.getArguments()[0]);
                System.out.println(status);
                return null;
            }
        }).when(main).setStatusString(Mockito.anyString());

        /** Inicializa componentes */
        IManager engineManager = ComponentFactory.createInstance();
        engineManager.setRequiredInterface("IGUIInterface", main);
        br.unicamp.ic.sed.historymgr.prov.IManager historyManager = br.unicamp.ic.sed.historymgr.impl.ComponentFactory.createInstance();
        br.unicamp.ic.sed.engine_history_connector.impl.IManager engineHistoryConnectorManager = br.unicamp.ic.sed.engine_history_connector.impl.ComponentFactory.createInstance();
        engineHistoryConnectorManager.setRequiredInterface("IHistory", historyManager.getProvidedInterface("IHistory"));
        engineManager.setRequiredInterface("IHistory", engineHistoryConnectorManager.getProvidedInterface("IHistory"));
        engineComponent = (IEngine) engineManager.getProvidedInterface("IEngine");
        engineComponent.setThreadStackSize(32768);

        boolean playerWhite = true;
        int ttLogSize = 16;
        engineComponent.newGame(playerWhite, ttLogSize, false);
    }

    public  void iniciaJogo() {
        moveIndex = 0;
        engineComponent.startGame();
    }

    public void humanoJoga(Move move) throws InvalidMoveException {
        engineComponent.humanMove(move);
        System.out.println(engineComponent.getFEN());
    }

    public void jogadaInvalida() throws InvalidMoveException {
        Move m = new Move(16, 24, 0);
        this.humanoJoga(m);
    }

    public void iaJoga() throws InterruptedException {
        engineComponent.startComputerMove();
        while (!engineComponent.humansTurn()){
            Thread.sleep(100);
        }
        System.out.println(engineComponent.getFEN());
    }

    public boolean vezDoHumano(){
        return engineComponent.humansTurn();
    }

    public void fimDeJogo() throws Exception {
        do{
            humanoJoga(jogadasValidas.get(i++));
            iaJoga();
        }while (!status.contains("Game over"));
    }

    public ChessController clone() {
        try {
            return (ChessController) super.clone();
        }
        catch(Exception e)
        {
            e.printStackTrace(java.lang.System.err);
        }
        return null;
    }

    public void handleEvent(Object... inColObject) {
        if(inColObject.length > 0){
            String sEventName = (String)inColObject[0];

            if(sEventName.compareTo("jogadaValidaEvent") == 0) {
                if (this.vezDoHumano()) {
                    try {
                        this.humanoJoga(jogadasValidas.get(moveIndex++));
                        state = State.IAJoga;
                        brancoJoga = !brancoJoga;
                    } catch (InvalidMoveException e) {
                        state = State.JogadaInvalida;
                    }
                }
                else {
                    try {
                        this.iaJoga();
                        state = State.HumanoJoga;
                        brancoJoga = !brancoJoga;
                    }catch (Exception e) {

                    }
                }
            } else if(sEventName.compareTo("jogadaInvalidaEvent") == 0) {
                if (this.vezDoHumano()) {
                    try {
                        this.humanoJoga(jogadasValidas.get(moveIndex++));
                        state = State.IAJoga;
                    } catch (InvalidMoveException e) {
                        state = State.JogadaInvalida;
                    }
                }
            } else if(sEventName.compareTo("inicarJogoEvent") == 0) {
                this.iniciaJogo();
            } else if(sEventName.compareTo("retornaJogadaInvalidaEvent") == 0) {
                if(State.JogadaInvalida.equals(state)) {
                    state = State.HumanoJoga;
                }
            } else if(sEventName.compareTo("fimDeJogoEvent") == 0){
                try {
                    this.fimDeJogo();
                } catch (Exception e){

                }
            }
        }

    }


}
