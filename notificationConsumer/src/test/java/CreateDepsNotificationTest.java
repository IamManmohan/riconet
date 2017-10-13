import depsNotification.DEPSNotificationConsumer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by ashfakh on 29/9/17.
 */
public class CreateDepsNotificationTest {


    @InjectMocks
    DEPSNotificationConsumer depsNotificationConsumer;




    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void processNotification(){
        String depsMessage="";
        String result=depsNotificationConsumer.processMessage(depsMessage);
        assertEquals(depsMessage,result);
    }

    @Test(expected = Exception.class)
    public void processNotificationException(){
        String result=depsNotificationConsumer.processMessage("test");
    }

}
