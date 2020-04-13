package com.example.vote.service.util;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.example.vote.dao.VoteActivityDao;
import com.example.vote.dao.VoteOptionDao;
import com.example.vote.entity.*;
import com.example.vote.service.IDistributedLockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @className: VoteUtils
 * @description
 * @author: luffy
 * @date: 2020/4/7 12:25
 * @version:V1.0
 */
@Component
public class VoteUtils {
    private static Logger logger = LoggerFactory.getLogger("VoteUtils.class");
    private static Lock lock = new ReentrantLock() ;
    private static RuntimeSchema<VoteActivity> activitySchema = RuntimeSchema.createFrom(VoteActivity.class);
    private static JedisPool jedisPool1;
    private static VoteActivityDao voteActivityDao1;
    private static VoteOptionDao voteOptionDao1;
    private static DataSourceTransactionManager dataSourceTransactionManager1;
    private static TransactionDefinition transactionDefinition1;
    private static IDistributedLockerService lockerService1;
    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager2;
    @Autowired
    TransactionDefinition transactionDefinition2;

    @Autowired
    private JedisPool jedisPool2;

    @Autowired
    private VoteActivityDao voteActivityDao2;
    @Autowired
    private VoteOptionDao voteOptionDao2;

    @Autowired
    private IDistributedLockerService lockerService2;

    @PostConstruct
    private void init() {
        jedisPool1 = jedisPool2;
        voteActivityDao1 = voteActivityDao2;
        voteOptionDao1 = voteOptionDao2;
        transactionDefinition1 = transactionDefinition2;
        dataSourceTransactionManager1 = dataSourceTransactionManager2;
        lockerService1 = lockerService2;
    }

    public static void setValue(String redisType, String key, String... value) {
        if (value == null || value.length <= 0) {
            return;
        }
        switch (redisType) {
            case IVoteConst.REDIS_STRING:
                redisStringSet(key, value[0]);
                break;
            case IVoteConst.REDIS_LIST:
                redisListSet(key, value[0]);
                break;
            case IVoteConst.REDIS_SET:
                redisSetSet(key, value[0]);
                break;
            case IVoteConst.REDIS_HASH:
                redisHashSet(key, value);
                break;
            case IVoteConst.REDIS_ZSET:
                redisZsetSet(key, value);
                break;
            default:
                break;
        }

    }


    public static void redisStringSet(String key, String value) {
        try (Jedis jedis = jedisPool1.getResource()) {
            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void redisListSet(String key, String value) {
        try (Jedis jedis = jedisPool1.getResource()) {
            jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void redisSetSet(String key, String value) {
        try (Jedis jedis = jedisPool1.getResource()) {
            jedis.sadd(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void redisHashSet(String key, String... value) {
        try (Jedis jedis = jedisPool1.getResource()) {
            if (value.length == 2) {
                jedis.hset(key, value[0], value[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void redisZsetSet(String key, String... value) {
        lock.lock();
        try (Jedis jedis = jedisPool1.getResource()) {
            if (value.length == 2) {
                jedis.zadd(key, Double.parseDouble(value[0]), value[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static Map<String, Double> getVoteRank(String key) {
        lock.lock();
        Map<String, Double> resMap = new HashMap<>();
        try (Jedis jedis = jedisPool1.getResource()) {
            Set<String> resSet = jedis.zrevrangeByScore(key, Double.MAX_VALUE, 0);
            if (resSet != null && resSet.size() > 0) {
                for (String tmpStr : resSet) {
                    Double score = jedis.zscore(key, tmpStr);
                    String[] resStr = tmpStr.split(":");
                    if (resStr != null && resStr.length == 3) {
                        resMap.put(resStr[1], score);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
        return resMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public static Integer buildVoteActivityDto(VoteActivityDto dto) {
        //插入记录
        VoteActivity voteActivity = new VoteActivity();
        voteActivity.setName(dto.getName())
                .setBegindatetime(dto.getBegindatetime())
                .setEnddatetime(dto.getEnddatetime());
        //开启事务
        TransactionStatus transactionStatus = dataSourceTransactionManager1.getTransaction(transactionDefinition1);

        try {
            //TODO 记录时间 - 区分时区
            // 创建活动已通过
            voteActivityDao1.insert(voteActivity);

            Integer aid = voteActivity.getId();
            List<VoteOption> optionList = dto.getOptionList();

            optionList = optionList.stream().map(option -> {
                option.setAid(aid);
                return option;
            }).collect(Collectors.toList());
            voteOptionDao1.batchInsert(optionList);
            //提交事务
            dataSourceTransactionManager1.commit(transactionStatus);
            return aid;
        } catch (Exception e) {
            e.printStackTrace();
            //数据回滚
            dataSourceTransactionManager1.rollback(transactionStatus);
        }
        return null;

    }


    public static VoteStatusEnum getDtoInfo(Integer aid) {
        String scoreKey = "vote:" + aid + ":score";
        String voteActivityKey = "vote:" + aid + ":activity";
        lock.lock();
        try (Jedis jedis = jedisPool1.getResource()) {
            byte[] voteActivityDetail = jedis.get(voteActivityKey.getBytes());
            if (voteActivityDetail != null && voteActivityDetail.length > 0) {
                VoteActivity voteActivity = activitySchema.newMessage();
                ProtostuffIOUtil.mergeFrom(voteActivityDetail, voteActivity, activitySchema);
                Date beginDateTime = voteActivity.getBegindatetime();
                Date enddatetime = voteActivity.getEnddatetime();
                LocalDateTime ldt = beginDateTime.toInstant()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toLocalDateTime();
                LocalDateTime edt = enddatetime.toInstant()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toLocalDateTime();

                if (ldt.isAfter(LocalDateTime.now())) {
                    //活动未开始
                    return VoteStatusEnum.getVoteStatus(IVoteConst.VOTENOTSTART_STATUS);
                } else if (edt.isBefore(LocalDateTime.now())) {
                    //活动已结束
                    return VoteStatusEnum.getVoteStatus(IVoteConst.VOTESTOPPED_STATUS);
                } else {
                    //活动进行中
                    return VoteStatusEnum.getVoteStatus(IVoteConst.VOTEDURING_STATUS);
                }
            } else {
                //活动已结束
                return VoteStatusEnum.getVoteStatus(IVoteConst.VOTESTOPPED_STATUS);
            }
        } catch (Exception e) {

        }finally {
            lock.unlock();
        }
        return VoteStatusEnum.getVoteStatus(IVoteConst.VOTESTOPPED_STATUS);

    }


    public static VoteStatusEnum getVoteStatus(Integer aid, String telphone) {

        //投票太快
        String tooFastKey = "vote:" + telphone + ":" + aid + ":fast";
        //重复投票
        String hashRepeatKey = "vote:" + aid + ":repeat";
        String repeatKey = "vote:" + telphone + ":" + aid + ":repeat";
        lock.lock();
        try (Jedis jedis = jedisPool1.getResource()) {
            String value = jedis.get(tooFastKey);
            //投票太快
            if (value != null) {
                return VoteStatusEnum.getVoteStatus(IVoteConst.VOTETOOFAST_STATUS);
            } else {
                String num = jedis.hget(hashRepeatKey, repeatKey);
                //重复投票
                if (num != null && Integer.parseInt(num) > IVoteConst.MAX_VOTES) {
                    return VoteStatusEnum.getVoteStatus(IVoteConst.VOTEREPEAT_STATUS);
                }//正常-100
                else {
                    jedis.setex(tooFastKey, IVoteConst.INTERVAL_SEC, "true");
                    if (num == null) {
                        jedis.hset(hashRepeatKey, repeatKey, "1");
                    }
                    jedis.hincrBy(hashRepeatKey, repeatKey, 1);
                    return VoteStatusEnum.getVoteStatus(IVoteConst.VOTEDEFAULT_STATUS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return VoteStatusEnum.getVoteStatus(IVoteConst.VOTEDEFAULT_STATUS);
    }


    public static void scoreIncr(String socreKey, Integer num, String scoreValue) {
        lockerService1.lock(UUID.randomUUID().toString(),()->{
            try (Jedis jedis = jedisPool1.getResource()) {
                jedis.zincrby(socreKey, num, scoreValue);
                logger.info("投票成功!");
                return null;
            }
        });

    }


    public static void voteStaticsDeal(Integer aid) {
        String scoreKey = "vote:" + aid + ":score";
        String voteActivityKey = "vote:" + aid + ":activity";

        try (Jedis jedis = jedisPool1.getResource()) {
            Map<String, Double> scoreMap = getVoteRank(scoreKey);
            List<VoteOption> optionList = voteOptionDao1.queryByAid(aid);
            if (optionList != null && optionList.size() > 0) {
                optionList = optionList.stream().map(e -> {
                    Integer score = scoreMap.get(e.getSerialnumber().toString()).intValue();
                    e.setScore(score);
                    return e;
                }).collect(Collectors.toList());
            }
            //更新数据
            voteOptionDao1.batchUpdate(optionList);
            //删除缓存数据
            delCache(aid);
        }
    }

    private static void delCache(Integer aid) {
        try (Jedis jedis = jedisPool1.getResource()) {
            String scoreKey = "vote:" + aid + ":score";
            String voteActivityKey = "vote:" + aid + ":activity";
            String hashRepeatKey = "vote:" + aid + ":repeat";

            jedis.del(scoreKey,voteActivityKey,hashRepeatKey);
        }

    }
}
