package com.example.vote.controller;

import com.example.vote.entity.IVoteConst;
import com.example.vote.entity.VoteActivity;
import com.example.vote.entity.VoteActivityDto;
import com.example.vote.entity.VoteStatusEnum;
import com.example.vote.service.VoteActivityService;
import com.example.vote.service.util.VoteUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * (VoteActivity)表控制层
 *
 * @author makejava
 * @since 2020-04-07 09:53:02
 */
@RestController
@RequestMapping("voteActivity")
public class VoteActivityController {

    private final Logger logger = Logger.getLogger(VoteActivityController.class.getName());
    /**
     * 服务对象
     */
    @Resource
    private VoteActivityService voteActivityService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public VoteActivity selectOne(Integer id) {
        return this.voteActivityService.queryById(id);
    }

    /**
     * 创建投票活动
     *
     * @param activity
     * @return
     */
    @PostMapping(value = "/createVote")
    public Map<String, Object> createVote(@RequestBody VoteActivityDto activity) {
        Map<String, Object> resMap = new HashMap<>();
        VoteActivityDto dto = voteActivityService.createVoteActivity(activity, VoteUtils::buildVoteActivityDto);
        if (dto == null) {
            resMap.put("msg", IVoteConst.CREATE_FAIL);
        } else {
            resMap.put("msg", IVoteConst.CREATE_SUCCESS);
        }
        resMap.put("data", dto);
        return resMap;
    }

    @GetMapping(value = "/showVoteDetail/{aid}")
    public Map<String, Object> showVoteDetail(@PathVariable Integer aid) {
        Map<String, Object> resMap = new HashMap<>();
        VoteActivityDto dto = voteActivityService.watchVoteActivity(aid, VoteUtils::getDtoInfo);
        resMap.put("msg", VoteUtils.getDtoInfo(aid).getDescription());
        resMap.put("data", dto);
        return resMap;
    }

    @GetMapping(value = "/doVote/{aid}")
    public Map<String, Object> doVote(@PathVariable("aid")Integer aid,@PathParam("sid") Integer sid,@PathParam("telphone")String telphone) {
        Map<String, Object> resMap = new HashMap<>();
        resMap = voteActivityService.doVote(aid,sid,telphone,VoteUtils::getVoteStatus);
        return resMap;
    }

    @GetMapping(value = "/voteStatics/{aid}")
    public Map<String, Object> voteStatics(@PathVariable("aid")Integer aid) {
        Map<String, Object> resMap = new HashMap<>();
        VoteActivityDto dto  = voteActivityService.voteStatics(aid,VoteUtils::voteStaticsDeal);
        resMap.put("msg", IVoteConst.STATICS_TIP);
        resMap.put("data", dto);
        return resMap;
    }

}