package com.example.vote.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.util.Date;
import java.io.Serializable;

/**
 * (VoteActivity)实体类
 *
 * @author makejava
 * @since 2020-04-07 09:53:02
 */
@Data
@Accessors(chain = true)
public class VoteActivity implements Serializable {
    private static final long serialVersionUID = -25612701556153310L;
    
    private Integer id;
    
    private String name;
    
    private Date begindatetime;
    
    private Date enddatetime;

    public VoteActivity() {
        super();
    }
}