package com.example.vote.service.impl;

import com.example.vote.entity.VoteOption;
import com.example.vote.dao.VoteOptionDao;
import com.example.vote.service.VoteOptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (VoteOption)表服务实现类
 *
 * @author makejava
 * @since 2020-04-07 15:48:03
 */
@Service("voteOptionService")
public class VoteOptionServiceImpl implements VoteOptionService {
    @Resource
    private VoteOptionDao voteOptionDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public VoteOption queryById(Integer id) {
        return this.voteOptionDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<VoteOption> queryAllByLimit(int offset, int limit) {
        return this.voteOptionDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param voteOption 实例对象
     * @return 实例对象
     */
    @Override
    public VoteOption insert(VoteOption voteOption) {
        this.voteOptionDao.insert(voteOption);
        return voteOption;
    }

    /**
     * 修改数据
     *
     * @param voteOption 实例对象
     * @return 实例对象
     */
    @Override
    public VoteOption update(VoteOption voteOption) {
        this.voteOptionDao.update(voteOption);
        return this.queryById(voteOption.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.voteOptionDao.deleteById(id) > 0;
    }
}