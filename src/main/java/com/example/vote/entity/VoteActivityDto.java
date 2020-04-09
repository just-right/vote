package com.example.vote.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @className: VoteActivityDto
 * @description
 * @author: luffy
 * @date: 2020/4/7 11:56
 * @version:V1.0
 */
@Data
@Accessors(chain = true)
public class VoteActivityDto implements Serializable {

    private static final long serialVersionUID = 7947839353259229990L;

    private Integer id;

    private String name;

    private Date begindatetime;

    private Date enddatetime;

    private List<VoteOption> optionList;

    public VoteActivityDto() {
        super();
    }
}
