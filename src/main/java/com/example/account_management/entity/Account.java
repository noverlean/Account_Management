package com.example.account_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Entity
@Data
@Getter
@Setter
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    public String toString()
    {
        return "=== ACCOUNT ================" +
                "id: " + id + ", \n\t" +
                "owner: " + owner.toString() + ", \n\t" +
                "amount: " + amount + ", \n\t" +
                "isBlocked: " + isBlocked;
    }
}
