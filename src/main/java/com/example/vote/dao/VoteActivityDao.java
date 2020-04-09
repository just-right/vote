package com.example.vote.dao;

import com.example.vote.entity.VoteActivity;
import com.example.vote.entity.VoteOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (VoteActivity)表数据库访问层
 *
 * @author makejava
 * @since 2020-04-07 09:53:02
 */
@Mapper
public interface VoteActivityDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    VoteActivity queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<VoteActivity> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param voteActivity 实例对象
     * @return 对象列表
     */
    List<VoteActivity> queryAll(VoteActivity voteActivity);

    /**
     * 新增数据
     *
     * @param voteActivity 实例对象
     * @return 影响行数
     */
    int insert(VoteActivity voteActivity);

    /**
     * 修改数据
     *
     * @param voteActivity 实例对象
     * @return 影响行数
     */
    int update(VoteActivity voteActivity);

    /**
     * 批量修改数据
     *
     * @param optionList 实例对象
     * @return 影响行数
     */
    int batchUpdate(List<VoteOption> optionList);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}