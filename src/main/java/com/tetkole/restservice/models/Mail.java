package com.tetkole.restservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // lombok, getter and setter
@Builder // lombok design partern builder
@AllArgsConstructor // constructor with all args
@NoArgsConstructor // constructor with no args
@Table(name= "mail")
public class Mail {

    @Column(name="mail_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mailId;

    @Column(name="mail",nullable=false)
    private String mail;

    public Mail(String mail) {
        this.mail = mail;
    }
}
