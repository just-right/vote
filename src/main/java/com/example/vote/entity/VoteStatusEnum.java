package com.example.vote.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum VoteStatusEnum {
    VOTEDEFAULT(100,"投票活动默认值"),
    VOTENOTSTART(101,"投票活动未开始"),
    VOTESTOPPED(102,"投票活动已结束"),
    VOTEREPEAT(103,"请勿重复投票"),
    VOTETOOFAST(104,"投票太快了"),
    VOTEDURING(105,"投票活动进行中");
    @Getter  private Integer status;
    @Getter  private String description;

    VoteStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public static VoteStatusEnum getVoteStatus(Integer status){
        Optional<VoteStatusEnum> optional  = Arrays.stream(VoteStatusEnum.values()).filter(voteStatusEnum->voteStatusEnum.getStatus().equals(status)).findFirst();
        return optional.orElse(VOTEDEFAULT);
    }
}
