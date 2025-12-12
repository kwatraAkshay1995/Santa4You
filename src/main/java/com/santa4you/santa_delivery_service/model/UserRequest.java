package com.santa4you.santa_delivery_service.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @Embedded
    @Valid
    @NotNull(message = "Address is required")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wish_list_id")
    private WishList wishList;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Return gift is required as part of the tradition")
    private ReturnGift returnGift;
}
