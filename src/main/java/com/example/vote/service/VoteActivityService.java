package com.example.vote.service;

import com.example.vote.entity.VoteActivity;
import com.example.vote.entity.VoteActivityDto;
import com.example.vote.entity.VoteStatusEnum;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * (VoteActivity)表服务接口
 *
 * @author makejava
 * @since 2020-04-07 09:53:02
 */
public interface VoteActivityService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    VoteActivity queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<VoteActivity> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param voteActivity 实例对象
     * @return 实例对象
     */
    VoteActivity insert(VoteActivity voteActivity);

    /**
     * 修改数据
     *
     * @param voteActivity 实例对象
     * @return 实例对象
     */
    VoteActivity update(VoteActivity voteActivity);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);


    /**
     * 创建投票活动
     * @param dto
     * @param function
     * @return
     */
    VoteActivityDto createVoteActivity(VoteActivityDto dto, Function<VoteActivityDto,Integer> function);

    /**
     * 查看投票活动信息
     * @param aid
     * @param function
     * @return
     */
    VoteActivityDto watchVoteActivity(Integer aid,Function<Integer, VoteStatusEnum> function);


    /**
     * 进行投票
     * @param aid
     * @param biFunction
     * @return
     */
    Map<String,Object> doVote(Integer aid, Integer sid, String telphone, BiFunction<Integer,String, VoteStatusEnum> biFunction);


    /**
     * 投票统计
     * @param aid
     * @param consumer
     * @return
     */
    VoteActivityDto voteStatics(Integer aid,Consumer<Integer> consumer);

}