import com.springboot.KafkaApplication;
import com.springboot.kafka.KafkaSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KafkaApplication.class})
public class SendKafka {

    @Autowired
    private KafkaSender kafkaSender;


    @Test
    public void sendMsg() throws Exception {
        kafkaSender.send();
    }
}
