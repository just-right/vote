package com.example.vote.service;

import com.example.vote.entity.VoteOption;
import java.util.List;

/**
 * (VoteOption)表服务接口
 *
 * @author makejava
 * @since 2020-04-07 15:48:03
 */
public interface VoteOptionService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    VoteOption queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<VoteOption> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param voteOption 实例对象
     * @return 实例对象
     */
    VoteOption insert(VoteOption voteOption);

    /**
     * 修改数据
     *
     * @param voteOption 实例对象
     * @return 实例对象
     */
    VoteOption update(VoteOption voteOption);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}