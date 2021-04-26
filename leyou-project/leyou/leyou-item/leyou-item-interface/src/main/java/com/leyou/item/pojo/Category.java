package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_category")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    @JsonIgnore
    private Long parentId;
@JsonIgnore
    private Boolean isParent;// 注意isParent生成的getter和setter方法需要手动加上Is
   @JsonIgnore
    private Integer sort;

    // getter和setter略

}