package cn.bugstack.test;

import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private RTopic dynamicThreadPoolRedisTopic;

    @Test
    public void test_dynamicThreadPoolRedisTopic() throws InterruptedException {
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity("dynamic-thread-pool-test-app", "threadPoolExecutor01");
        threadPoolConfigEntity.setPoolSize(100);
        threadPoolConfigEntity.setMaximumPoolSize(100);
        dynamicThreadPoolRedisTopic.publish(threadPoolConfigEntity);

        new CountDownLatch(1).await();
    }

    /* Write Code Here */
    public String removeRepeatChar(String str) {
        char[] charArray = str.toCharArray();
        Set<Character> set = new HashSet<>();
        StringBuilder res = new StringBuilder();
        for(char c:charArray){
            if (!set.contains(c)){
                res.append(c);
                set.add(c);
            }
        }
        return res.toString();


    }


}
