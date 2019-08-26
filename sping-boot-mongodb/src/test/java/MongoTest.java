import com.alibaba.fastjson.JSONObject;
import com.common.MongoApplication;
import com.common.mongo.BaseDao;
import com.common.mongo.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoApplication.class)
public class MongoTest {

    @Resource
    private BaseDao baseDao;

    @Test
    public void testMongoGet(){
        List<Student> studentList = baseDao.findAll(Student.class);
        log.error("list ==========:{}", JSONObject.toJSONString(studentList));
    }

    @Test
    public void testMongoSave(){
        Student student = new Student();
        student.setAge(2);
        student.setLike("image1");
        student.setStar("101");
        student.setName("张三1");
        student.setFimalyAddrs("上海市上海");
        baseDao.save(student);
    }
}
