package com.ainbondhu.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nameBn; // e.g., "ডিভোর্স" (Divorce)
    private String descriptionBn;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Question> questions;
}
