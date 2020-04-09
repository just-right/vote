package com.example.vote.entity;

public interface IVoteConst {
    Integer MAX_VOTES = 5;
    Integer INTERVAL_SEC = 5;
    String REDIS_STRING = "string";
    String REDIS_LIST = "list";
    String REDIS_SET = "set";
    String REDIS_HASH = "hash";
    String REDIS_ZSET = "zset";
    String STATICS_TIP = "统计成功";
    String CREATE_SUCCESS = "投票活动创建成功";
    String CREATE_FAIL = "投票活动创建失败";

    Integer VOTEDEFAULT_STATUS = 100;  //投票活动默认值
    Integer VOTENOTSTART_STATUS = 101; //投票活动未开始
    Integer VOTESTOPPED_STATUS = 102;  //投票活动已结束
    Integer VOTEREPEAT_STATUS = 103;   //请勿重复投票
    Integer VOTETOOFAST_STATUS = 104;  //投票太快了
    Integer VOTEDURING_STATUS = 105;  //投票活动进行中





}
