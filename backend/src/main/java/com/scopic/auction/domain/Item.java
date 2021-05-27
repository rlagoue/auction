package com.scopic.auction.domain;

import com.scopic.auction.dto.ItemDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_item")
public class Item {
    @Id
    private long id;
    @Column(name = "c_name")
    private String name;
    @Column(name = "c_description")
    private String description;

    public Item() {
    }

    public Item(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ItemDto toDto() {
        return  new ItemDto(id, name, description);
    }
}
