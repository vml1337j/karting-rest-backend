package vml1337j.sws.rest.store.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "result")
public class ResultEntity {

    @EmbeddedId
    private RacerEventPK id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @MapsId("racerId")
    @JoinColumn(name = "racer_id")
    private RacerEntity racer;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    private int position;

    public ResultEntity(RacerEntity racer, EventEntity event, int position) {
        this.id = new RacerEventPK(racer.getId(), event.getId());
        this.racer = racer;
        this.event = event;
        this.position = position;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class RacerEventPK implements Serializable {
        @Column(name = "racer_id")
        private Long racerId;
        @Column(name = "event_id")
        private Long eventId;
    }
}
