import { useContext, useEffect } from 'react';
import { css } from '@emotion/react';
import MeetingItem from 'components/MeetingItem';
import MeetingItemSkeleton from 'components/MeetingItemSkeleton';
import CoffeeStackItem from 'components/CoffeeStackItem';
import CoffeeStackItemSkeleton from 'components/CoffeeStackItemSkeleton';
import ErrorIcon from 'components/@shared/ErrorIcon';
import ReloadButton from 'components/@shared/ReloadButton';
import useQuery from 'hooks/useQuery';
import useTimer from 'hooks/useTimer';
import NoSearchResultIconSVG from 'assets/NoSearchResult.svg';
import { getMeetingListApi } from 'apis/meetingApis';
import { getServerTime } from 'apis/common';
import { userContext, UserContextValues } from 'contexts/userContext';
import * as S from './MeetingListPage.styled';

const MeetingListPage = () => {
  const userState = useContext(userContext) as UserContextValues;
  const { data: serverTimeResponse } = useQuery(['serverTime'], getServerTime);

  const {
    data: meetingListResponse,
    refetch,
    isLoading,
    isError,
  } = useQuery(['meetingList'], getMeetingListApi(), {
    enabled: !!userState.accessToken,
  });

  const { currentTimestamp } = useTimer(
    serverTimeResponse?.body.serverTime || Date.now()
  );

  const currentDate = new Date(currentTimestamp);
  const currentLocaleTimeString = currentDate.toLocaleTimeString(undefined, {
    hourCycle: 'h24',
    hour: '2-digit',
    minute: '2-digit',
  });

  const activeMeetings = meetingListResponse?.body.meetings.filter(
    ({ isActive }) => isActive
  );
  const inactiveMeetings = meetingListResponse?.body.meetings.filter(
    ({ isActive }) => !isActive
  );
  const sortedMeetings = [
    ...(activeMeetings || []),
    ...(inactiveMeetings || []),
  ];

  useEffect(() => {
    const closingMeeting = sortedMeetings.find(
      (meeting) =>
        meeting.isActive &&
        meeting.upcomingEvent?.attendanceClosedTime === currentLocaleTimeString
    );

    if (closingMeeting) {
      refetch();
    }
  }, [currentLocaleTimeString]);

  if (isLoading && !meetingListResponse?.body) {
    return (
      <S.Layout>
        <S.TimeSection>
          <S.DateBox>
            <S.Title>Today</S.Title>
            <S.EmptyStateDateParagraph />
          </S.DateBox>
          <S.DateBox>
            <S.Title>Time</S.Title>
            <S.EmptyStateTimeParagraph />
          </S.DateBox>
        </S.TimeSection>
        <S.MeetingListSection>
          <S.TitleBox>
            <S.Title>참여 중인 모임</S.Title>
          </S.TitleBox>
          <S.MeetingList
            css={css`
              overflow: hidden;
            `}
          >
            {Array.from({ length: 2 }).map((_, id) => (
              <li key={id}>
                <MeetingItemSkeleton />
              </li>
            ))}
          </S.MeetingList>
        </S.MeetingListSection>
        <S.CoffeeStackSection>
          <S.TitleBox>
            <S.Title>커피 스택</S.Title>
          </S.TitleBox>
          <S.CoffeeStackList>
            {Array.from({ length: 3 }).map((_, id) => (
              <li key={id}>
                <CoffeeStackItemSkeleton />
              </li>
            ))}
          </S.CoffeeStackList>
        </S.CoffeeStackSection>
      </S.Layout>
    );
  }

  if (isError) {
    return (
      <S.Layout>
        <S.ErrorBox>
          <ErrorIcon />
          <ReloadButton
            onClick={() => {
              refetch();
            }}
          />
        </S.ErrorBox>
      </S.Layout>
    );
  }

  if (meetingListResponse?.body.meetings.length === 0) {
    return (
      <S.Layout>
        <S.EmptyStateBox>
          <S.EmptyStateImage src={NoSearchResultIconSVG} alt="no meetings" />
          <S.EmptyStateTitle>아직 모임이 없어요.</S.EmptyStateTitle>
          <S.EmptyStateParagraph>
            직접 모임을 등록하러 가볼까요?
          </S.EmptyStateParagraph>
          <S.MeetingCreateLink to="/meeting/create">
            모임 생성하기
          </S.MeetingCreateLink>
        </S.EmptyStateBox>
      </S.Layout>
    );
  }

  return (
    <S.Layout>
      <S.TimeSection>
        <S.DateBox>
          <S.Title>Today</S.Title>
          <S.DateParagraph>
            {currentDate.toLocaleDateString(undefined, {
              weekday: 'long',
              month: 'long',
              day: 'numeric',
            })}
          </S.DateParagraph>
        </S.DateBox>
        <S.DateBox>
          <S.Title>Time</S.Title>
          <S.DateParagraph>
            {currentDate.toLocaleTimeString(undefined, {
              hourCycle: 'h24',
              hour: '2-digit',
              minute: '2-digit',
              second: '2-digit',
            })}
          </S.DateParagraph>
        </S.DateBox>
      </S.TimeSection>
      <S.MeetingListSection>
        <S.TitleBox>
          <S.Title>참여 중인 모임</S.Title>
          <S.PageLink aria-label="모임 생성하기" to="/meeting/create">
            생성하기
          </S.PageLink>
        </S.TitleBox>
        <S.MeetingList>
          {sortedMeetings.map((meeting) => (
            <li key={meeting.id}>
              <MeetingItem meeting={meeting} />
            </li>
          ))}
        </S.MeetingList>
      </S.MeetingListSection>
      <S.CoffeeStackSection>
        <S.TitleBox>
          <S.Title>나의 커피 스택</S.Title>
        </S.TitleBox>
        <S.CoffeeStackList>
          {meetingListResponse?.body.meetings.map((meeting) => (
            <li key={meeting.id}>
              <CoffeeStackItem
                name={meeting.name}
                tardyCount={meeting.tardyCount}
              />
            </li>
          ))}
        </S.CoffeeStackList>
      </S.CoffeeStackSection>
    </S.Layout>
  );
};

export default MeetingListPage;
