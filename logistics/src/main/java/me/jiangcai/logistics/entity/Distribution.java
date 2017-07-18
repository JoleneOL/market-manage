package me.jiangcai.logistics.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * 可追踪配送
 *
 * @author CJ
 */
@Setter
@Getter
public class Distribution {

    @Id
    @Column(length = 64)
    private String id;

}
