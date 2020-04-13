package com.example.vote.service.impl;

import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.example.vote.dao.VoteActivityDao;
import com.example.vote.dao.VoteOptionDao;
import com.example.vote.entity.*;
import com.example.vote.service.VoteActivityService;
import com.example.vote.service.util.ProtoStuffUtils;
import com.example.vote.service.util.VoteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * (VoteActivity)表服务实现类
 *
 * @author makejava
 * @since 2020-04-07 09:53:02
 */
@Service("voteActivityService")
public class VoteActivityServiceImpl implements VoteActivityService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private RuntimeSchema<VoteActivityDto> activitySchema = RuntimeSchema.createFrom(VoteActivityDto.class);
    @Resource
    private VoteActivityDao voteActivityDao;

    @Resource
    private VoteOptionDao voteOptionDao;

    @Resource
    private JedisPool jedisPool;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public VoteActivity queryById(Integer id) {
        return this.voteActivityDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<VoteActivity> queryAllByLimit(int offset, int limit) {
        return this.voteActivityDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param voteActivity 实例对象
     * @return 实例对象
     */
    @Override
    public VoteActivity insert(VoteActivity voteActivity) {
        this.voteActivityDao.insert(voteActivity);
        return voteActivity;
    }

    /**
     * 修改数据
     *
     * @param voteActivity 实例对象
     * @return 实例对象
     */
    @Override
    public VoteActivity update(VoteActivity voteActivity) {
        this.voteActivityDao.update(voteActivity);
        return this.queryById(voteActivity.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.voteActivityDao.deleteById(id) > 0;
    }

    @Override
    public VoteActivityDto createVoteActivity(VoteActivityDto dto, Function<VoteActivityDto, Integer> function) {
        //插入记录
        Integer aid = function.apply(dto);

        //存入缓存
        String scoreKey = "vote:" + aid + ":score";
        String voteActivityKey = "vote:" + aid + ":activity";
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date beginDateTime = dto.getBegindatetime();
        Date endDateTime = dto.getEnddatetime();

        Long expire = endDateTime.getTime() - beginDateTime.getTime();

        List<VoteOption> optionList = dto.getOptionList();

        byte[] activityDtoBytes = ProtoStuffUtils.serialize(dto);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(voteActivityKey.getBytes(), Integer.parseInt(expire.toString()), activityDtoBytes);
            //TODO 空指针
            optionList.stream().forEach(e -> {
                String tmpValue = "vote:" + e.getSerialnumber() + ":option";
                VoteUtils.setValue(IVoteConst.REDIS_ZSET, scoreKey, String.valueOf(e.getScore()), tmpValue);
            });
        }

        return dto;
    }

    @Override
    public VoteActivityDto watchVoteActivity(Integer aid, Function<Integer, VoteStatusEnum> function) {

        Integer status = function.apply(aid).getStatus();

        return dealVoteDetail(aid, status);
    }

    @Override
    public Map<String, Object> doVote(Integer aid, Integer sid, String telphone, BiFunction<Integer, String, VoteStatusEnum> biFunction) {
        //只用考虑投票正在进行中
        Map<String, Object> resMap = new HashMap<>();

        VoteStatusEnum status = VoteUtils.getDtoInfo(aid);
        if (!status.getStatus().equals(IVoteConst.VOTEDURING_STATUS)) {
            resMap.put("data", null);
            resMap.put("msg", VoteStatusEnum.getVoteStatus(IVoteConst.VOTEDEFAULT_STATUS).getDescription());
            return resMap;
        }

        VoteStatusEnum statusEnum = biFunction.apply(aid, telphone);
        String scoreKey = "vote:" + aid + ":score";
        String scoreValue = "vote:" + sid + ":option";
        if (statusEnum.getStatus().equals(IVoteConst.VOTEDEFAULT_STATUS)) {
            VoteUtils.scoreIncr(scoreKey, 1, scoreValue);
            logger.info("投票成功！");
        }
        VoteActivityDto resDto = dealVoteDetail(aid, statusEnum.getStatus());
        resMap.put("msg", statusEnum.getDescription());
        resMap.put("data", resDto);

        return resMap;
    }

    @Override
    public VoteActivityDto voteStatics(Integer aid, Consumer<Integer> consumer) {
        consumer.accept(aid);
        VoteActivity activity = voteActivityDao.queryById(aid);
        List<VoteOption> optionList = voteOptionDao.queryByAid(aid);
        voteOptionDao.batchUpdate(optionList);

        VoteActivityDto dto = new VoteActivityDto();
        dto.setBegindatetime(activity.getBegindatetime())
                .setEnddatetime(activity.getEnddatetime())
                .setName(activity.getName())
                .setId(activity.getId())
                .setOptionList(optionList);

        return dto;
    }

    private VoteActivityDto dealVoteDetail(Integer aid, Integer status) {
        String scoreKey = "vote:" + aid + ":score";
        String voteActivityKey = "vote:" + aid + ":activity";

        VoteActivityDto dto = detailCache(voteActivityKey, scoreKey, status);

        return dto;

    }

    private VoteActivityDto detailCache(String key, String scoreKey, Integer status) {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            //从缓存获取活动详情数据
            byte[] voteActivityDtoBytes = jedis.get(key.getBytes());
            if (voteActivityDtoBytes != null && voteActivityDtoBytes.length > 0) {
                VoteActivityDto voteActivityDto = ProtoStuffUtils.deSerialize(voteActivityDtoBytes, VoteActivityDto.class);
                //活动未开始
                if (status.equals(IVoteConst.VOTENOTSTART_STATUS)) {
                    return voteActivityDto;
                }//活动进行中
                else {
                    List<VoteOption> optionList = voteActivityDto.getOptionList();
                    //序号-分数
                    Map<String, Double> scoreMap = VoteUtils.getVoteRank(scoreKey);

                    optionList = optionList.stream().map(e -> {
                        if (scoreMap.containsKey(String.valueOf(e.getSerialnumber()))) {
                            Integer score = scoreMap.get(String.valueOf(e.getSerialnumber())).intValue();
                            e.setScore(score);
                        }
                        return e;
                    }).collect(Collectors.toList());
                    return voteActivityDto;
                }
            }
        } finally {
            readLock.unlock();
        }
        //活动已结束
        return null;
    }
}