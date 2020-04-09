package com.example.vote.controller;

import com.example.vote.entity.VoteOption;
import com.example.vote.service.VoteOptionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (VoteOption)表控制层
 *
 * @author makejava
 * @since 2020-04-07 15:48:04
 */
@RestController
@RequestMapping("voteOption")
public class VoteOptionController {
    /**
     * 服务对象
     */
    @Resource
    private VoteOptionService voteOptionService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public VoteOption selectOne(Integer id) {
        return this.voteOptionService.queryById(id);
    }

}