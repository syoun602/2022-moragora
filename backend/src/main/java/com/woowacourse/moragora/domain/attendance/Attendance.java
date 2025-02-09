package com.woowacourse.moragora.domain.attendance;

import com.woowacourse.moragora.domain.event.Event;
import com.woowacourse.moragora.domain.participant.Participant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "boolean default false")
    private Boolean disabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public Attendance(final Status status,
                      final Boolean disabled,
                      final Participant participant,
                      final Event event) {
        this.status = status;
        this.disabled = disabled;
        this.participant = participant;
        this.event = event;
    }

    public void changeAttendanceStatus(final Status status) {
        this.status = status;
    }

    public void disable() {
        disabled = true;
    }

    public boolean isTardy() {
        return this.status == Status.TARDY;
    }

    public boolean isNone() {
        return this.status == Status.NONE;
    }

    /**
     * 우디
     * @return
     */
    public boolean isEnabled() {
        return !disabled;
    }
}
