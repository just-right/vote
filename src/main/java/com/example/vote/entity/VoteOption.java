package com.example.vote.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * (VoteOption)实体类
 *
 * @author makejava
 * @since 2020-04-07 15:48:01
 */
@Data
public class VoteOption implements Serializable {
    private static final long serialVersionUID = -36272093141959695L;
    
    private Integer id;
    
    private Integer serialnumber;
    
    private String name;
    
    private Integer aid;
    
    private Integer score;


    public VoteOption() {
        super();
    }
}