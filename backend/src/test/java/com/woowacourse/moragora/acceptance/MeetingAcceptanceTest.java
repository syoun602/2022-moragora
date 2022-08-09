package com.woowacourse.moragora.acceptance;

import static com.woowacourse.moragora.support.MeetingFixtures.MORAGORA;
import static com.woowacourse.moragora.support.UserFixtures.MASTER;
import static com.woowacourse.moragora.support.UserFixtures.createUsers;
import static org.hamcrest.Matchers.notNullValue;

import com.woowacourse.moragora.dto.MeetingRequest;
import com.woowacourse.moragora.entity.Meeting;
import com.woowacourse.moragora.support.ServerTimeManager;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@DisplayName("모임 관련 기능")
public class MeetingAcceptanceTest extends AcceptanceTest {

    @MockBean
    private ServerTimeManager serverTimeManager;

    @DisplayName("사용자가 모임을 등록하고 상태코드 200 OK 를 반환받는다.")
    @Test
    void add() {
        // given
        final List<Long> userIds = saveUsers(createUsers());

        final Meeting meeting = MORAGORA.create();
        final MeetingRequest meetingRequest = MeetingRequest.builder()
                .name(meeting.getName())
                .userIds(userIds)
                .build();

        // when
        final ValidatableResponse response = post("/meetings", meetingRequest, signUpAndGetToken(MASTER.create()));

        // then
        response.statusCode(HttpStatus.CREATED.value())
                .header("Location", notNullValue());
    }

//    @DisplayName("사용자가 특정 모임을 조회하면 해당 모임 상세 정보와 상태코드 200을 반환한다.")
//    @Test
//    void findOne() {
//        // given
//        final String token = signUpAndGetToken(MASTER.create());
//        final List<User> users = createUsers();
//        final List<String> userEmails = users.stream()
//                .map(User::getEmail)
//                .collect(Collectors.toList());
//
//        final List<String> userNames = users.stream()
//                .map(User::getNickname)
//                .collect(Collectors.toList());
//
//        final List<Long> userIds = saveUsers(users);
//        final Meeting meeting = MORAGORA.create();
//        final int meetingId = saveMeeting(token, userIds, meeting);
//
//        userIds.add(1L);
//        userNames.add(MASTER.getNickname());
//        userEmails.add(MASTER.getEmail());
//
//        final List<Integer> ids = userIds.stream()
//                .map(Long::intValue)
//                .collect(Collectors.toList());
//
//        given(serverTimeManager.getDate())
//                .willReturn(LocalDate.of(2022, 8, 1));
//
//        // when
//        final ValidatableResponse response = get("/meetings/" + meetingId, token);
//
//        // then
//        response.statusCode(HttpStatus.OK.value())
//                .body("id", equalTo(meetingId))
//                .body("name", equalTo(meeting.getName()))
//                .body("attendanceCount", equalTo(1))
//                .body("isActive", equalTo(true))
//                .body("hasUpcomingEvent", equalTo(true))
//                .body("users.id", equalTo(ids))
//                .body("users.nickname", equalTo(userNames))
//                .body("users.email", equalTo(userEmails));
//    }
//
//    @DisplayName("사용자가 자신이 속한 모든 모임을 조회하면 모임 정보와 상태코드 200을 반환한다.")
//    @Test
//    void findMy() {
//        // given
//        final String token = signUpAndGetToken(MASTER.create());
//        final Meeting meeting1 = MORAGORA.create();
//        final Meeting meeting2 = F12.create();
//
//        final User user = KUN.create();
//        final List<Long> ids = saveUsers(List.of(user));
//
//        final int meetingId1 = saveMeeting(token, ids, meeting1);
//        final int meetingId2 = saveMeeting(token, ids, meeting2);
//
//        given(serverTimeManager.isAttendanceTime(LocalTime.of(10, 0)))
//                .willReturn(false);
//        given(serverTimeManager.isAttendanceTime(LocalTime.of(9, 0)))
//                .willReturn(true);
//        given(serverTimeManager.isOverClosingTime(any(LocalTime.class)))
//                .willReturn(true);
//        given(serverTimeManager.calculateClosingTime(LocalTime.of(10, 0)))
//                .willReturn(LocalTime.of(10, 5));
//        given(serverTimeManager.calculateClosingTime(LocalTime.of(9, 0)))
//                .willReturn(LocalTime.of(9, 5));
//
//        // when
//        final ValidatableResponse response = get("/meetings/me", token);
//
//        // then
//        response.statusCode(HttpStatus.OK.value())
//                .body("meetings.id", containsInAnyOrder(meetingId1, meetingId2))
//                .body("meetings.name", containsInAnyOrder(meeting1.getName(), meeting2.getName()))
//                .body("meetings.tardyCount", containsInAnyOrder(0, 0));
//    }
}
