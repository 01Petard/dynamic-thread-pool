package cn.bustack.middleware.dynamic.thread.pool;

import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import org.junit.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 单元测试
 * @create 2024-05-12 15:38
 */
@SpringBootTest
public class ApiTest {

    @Autowired
    private RedisRegistry registry;

    private RedissonClient redissonClient;

    @Test
    public void test123() {
        RedisRegistry registry = new RedisRegistry(redissonClient);
        String key = RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey();
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(key);
        System.out.println(list);
    }

}
