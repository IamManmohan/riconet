import consumerOne.ConsumerOne;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by ashfakh on 29/9/17.
 */
public class ConsumerTest {

    @InjectMocks
    ConsumerOne consumerOne;


    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void processMessage(){
        String str=consumerOne.processMessage("test");
        assertEquals("test",str);
    }

//    @Test(expected = Exception.class)
//    public void processMessageException(){
//
//    }

}
