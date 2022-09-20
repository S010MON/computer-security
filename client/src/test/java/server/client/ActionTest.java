package server.client;

import org.junit.jupiter.api.Test;
import server.client.gui.Action;
import server.client.gui.Direction;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionTest
{
    @Test
    void testActionString_INCREASE()
    {
        Action a = new Action(Direction.INCREASE, 10);
        assertEquals("\"INCREASE 10\"", a.toString());
    }

    @Test
    void testActionString_DECREASE()
    {
        Action a = new Action(Direction.DECREASE, 10);
        assertEquals("\"DECREASE 10\"", a.toString());
    }
}
