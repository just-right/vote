package com.example.vote.dao;

import com.example.vote.entity.VoteOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (VoteOption)表数据库访问层
 *
 * @author makejava
 * @since 2020-04-07 15:48:02
 */
@Mapper
public interface VoteOptionDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    VoteOption queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<VoteOption> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param voteOption 实例对象
     * @return 对象列表
     */
    List<VoteOption> queryAll(VoteOption voteOption);

    /**
     * 新增数据
     *
     * @param voteOption 实例对象
     * @return 影响行数
     */
    int insert(VoteOption voteOption);

    /**
     * 批量插入
     * @param optionList
     * @return
     */
    int batchInsert(@Param("optionList") List<VoteOption> optionList);

    /**
     * 修改数据
     *
     * @param voteOption 实例对象
     * @return 影响行数
     */
    int update(VoteOption voteOption);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 通过活动主键查询数据
     *
     * @param aid 活动主键
     */
    List<VoteOption> queryByAid(Integer aid);


    /**
     * 批量更新投票选项
     *
     * @param optionList 活动主键
     */
    int batchUpdate(@Param("optionList") List<VoteOption> optionList);





}