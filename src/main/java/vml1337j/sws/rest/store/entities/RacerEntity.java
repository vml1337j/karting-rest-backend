package vml1337j.sws.rest.store.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "racer")
public class RacerEntity {

    @Id
    @Column(unique = true)
    private Long id;

    @Column(unique = true)
    private String swsId;

    private String firstname;

    private String lastname;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String country;

    private Long points;

    @Column(unique = true)
    private String profileUrl;

    @Column(name = "is_filled")
    private boolean isFilled;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "racer", cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<ResultEntity> racerEvents = new ArrayList<>();

    public List<EventEntity> getEvents() {
        return getRacerEvents().stream()
                .map(ResultEntity::getEvent)
                .collect(Collectors.toList());
    }

    public enum Gender {
        MALE,
        FEMALE;
    }
}
