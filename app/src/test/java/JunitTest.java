import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Created by thiago on 6/21/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class JunitTest {

    @Test
    public void test1()
    {
        ChessController oTestObject = new ChessController();
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));
        oTestObject.handleEvent("jogadaValidaEvent");
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == false));
        oTestObject.handleEvent("fimDeJogoEvent");

    }

    @Test
    public void test2()
    {
        ChessController oTestObject = new ChessController();
        Boolean brancoJoga_init1 = true;
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));
        oTestObject.handleEvent("iniciarJogoEvent", brancoJoga_init1);
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == brancoJoga_init1.booleanValue()));
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));
        oTestObject.handleEvent("jogadaInvalidaEvent");
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));
        oTestObject.handleEvent("retornaJogadaInvalidaEvent");
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));

    }

    @Test
    public void test3()
    {
        ChessController oTestObject = new ChessController();
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));
        oTestObject.handleEvent("jogadaValidaEvent");
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == false));
        oTestObject.handleEvent("jogadaValidaEvent");
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));

    }

    @Test
    public void test4()
    {
        ChessController oTestObject = new ChessController();
        assertEquals(true, (oTestObject.brancoJoga.booleanValue() == true));
        oTestObject.handleEvent("fimDeJogoEvent");

    }
}
