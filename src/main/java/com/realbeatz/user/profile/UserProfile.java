package com.realbeatz.user.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.realbeatz.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static com.realbeatz.util.UserUtils.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "user_profiles")
public class UserProfile {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profiles_gen")
    @SequenceGenerator(name = "user_profiles_gen", sequenceName = "user_profiles_seq", initialValue = 1000, allocationSize = 1)
    @Column(name = "profile_id", nullable = false)
    private Long id;

    @OneToOne(mappedBy = "profile")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @Column(length = MAX_LAST_NAME_LENGTH, nullable = false)
    private String lastName;

    @Column(length = MAX_FIRST_NAME_LENGTH, nullable = false)
    private String firstName;
    private LocalDate dob;

    @Transient
    private Integer age;

    @Column(length = MAX_BIO_LENGTH)
    @Builder.Default
    private String bio = "";

    public Integer getAge() {
        if (dob == null) return null;

        LocalDate now = LocalDate.now();
        Integer age = now.getYear() - dob.getYear();
        if (now.getDayOfYear() < dob.getDayOfYear()) age--;
        return age;
    }
}
