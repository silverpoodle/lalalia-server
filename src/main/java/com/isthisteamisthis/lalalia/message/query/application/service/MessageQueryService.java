package com.isthisteamisthis.lalalia.message.query.application.service;

import com.isthisteamisthis.lalalia.message.command.domain.aggregate.entity.Message;
import com.isthisteamisthis.lalalia.message.command.domain.aggregate.vo.GetUserNoVO;
import com.isthisteamisthis.lalalia.message.command.domain.aggregate.vo.SendUserNoVO;
import com.isthisteamisthis.lalalia.message.query.application.dto.response.GetAllMessageResponse;
import com.isthisteamisthis.lalalia.message.query.application.dto.response.GetMessageListResponse;
import com.isthisteamisthis.lalalia.message.query.application.dto.response.GetMessageResponse;
import com.isthisteamisthis.lalalia.message.query.domain.repository.MessageQueryRepository;
import com.isthisteamisthis.lalalia.message.query.infrastructure.service.ApiUserMessageQueryService;
import com.isthisteamisthis.lalalia.user.query.application.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageQueryService {

    private final MessageQueryRepository messageQueryRepository;
    private final ApiUserMessageQueryService apiUserMessageQueryService;

    // 사용자가 받은 메세지 리스트 조회
    @Transactional(readOnly = true)
    public GetAllMessageResponse getAllReceivedMessage(UserResponse user) {
        // 현재 사용자의 userNo로 getUserNoVO 생성
        GetUserNoVO getUserNoVO = new GetUserNoVO(user.getUserNo());
        // getUserNoVO로 message 리스트 조회
        List<Message> messageList = messageQueryRepository.findMessagesByGetUserNoVOOrderByDateDesc(getUserNoVO);

        return getGetAllMessageResponse(messageList);

    }

    // 사용자가 보낸 메세지 리스트 조회
    @Transactional(readOnly = true)
    public GetAllMessageResponse getAllSentMessage(UserResponse user) {
        // 현재 사용자의 userNo로 getUserNoVO 생성
        SendUserNoVO sendUserNoVO = new SendUserNoVO(user.getUserNo());
        // getUserNoVO로 message 리스트 조회
        List<Message> messageList = messageQueryRepository.findMessagesBySendUserNoVOOrderByDateDesc(sendUserNoVO);
        //  message list 에 nickname 을 추가해서 getMessageListResponse list 로 변환 -> 변환한 list 를 getAllMessageResponse 로 변환
        return getGetAllMessageResponse(messageList);

    }

    // 하나의 메세지 상세 조회
    @Transactional(readOnly = true)
    public GetMessageResponse getMessage(UserResponse user, Long messageNo) {
        // messageNo로 메세지 조회
        Message message = messageQueryRepository.findByMessageNoAndGetUserNo(messageNo, user.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("Invalid MessageNo"));
        // 메세지를 보낸 사용자 조회
        UserResponse sendUser = apiUserMessageQueryService.getUserByUserNo(message.getSendUserNoVO().getSendUserNo());
        // 메시지를 보낸 사용자와 받은 사용자의 닉네임
        String getUserNickname = user.getNickname();
        String sendUserNickname = sendUser.getNickname();

        return GetMessageResponse.from(message, getUserNickname, sendUserNickname);

    }

    // message list 에 nickname 을 추가해서 getMessageListResponse list 로 변환
    private GetAllMessageResponse getGetAllMessageResponse(List<Message> messageList) {
        List<GetMessageListResponse> response = messageList.stream().map(
                message -> {

                    Long sendUserNo = message.getSendUserNoVO().getSendUserNo();
                    Long getUserNo = message.getGetUserNoVO().getGetUserNo();

                    String sendUserNickname = apiUserMessageQueryService.getNicknameByUserNo(sendUserNo);
                    String getUserNickname = apiUserMessageQueryService.getNicknameByUserNo(getUserNo);

                    return GetMessageListResponse.from(message, sendUserNickname, getUserNickname);

                }).collect(Collectors.toList());

        return GetAllMessageResponse.from(response);
    }
}
