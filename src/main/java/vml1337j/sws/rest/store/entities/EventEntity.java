package vml1337j.sws.rest.store.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    @Column(unique = true)
    private Long id;

    @Column(unique = true)
    private String title;

    private String category;

    private LocalDate date;

    private Integer numberOfRacers;

    @Column(unique = true)
    private String url;

    @Column(name = "is_filled")
    private boolean isFilled;

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<ResultEntity> results = new ArrayList<>();

    public void addRacers(List<RacerEntity> racers) {
        for (int i = 0; i < racers.size(); ) {
            addRacer(racers.get(i), ++i);
        }
    }

    public void addRacer(RacerEntity racer, int position) {
        ResultEntity racerEvent = new ResultEntity(racer, this, position);
        getResults().add(racerEvent);
        racer.getResults().add(racerEvent);
    }

    public List<RacerEntity> getRacers() {
        return getResults().stream()
                .map(ResultEntity::getRacer)
                .collect(Collectors.toList());
    }
}
