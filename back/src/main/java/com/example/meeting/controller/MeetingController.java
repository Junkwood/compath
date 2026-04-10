package com.example.meeting.controller;

import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.service.AttachmentService;
import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import com.example.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MeetingController {

    private final MeetingService service;
    private final AttachmentService attachmentService;

    //    회의록 유형 조회
    @GetMapping("/meeting/register/typeList")
    public List<MeetingDTO> getMeetingType() {
        return service.getMeetingType();
    }

    //    회의록 등록
    @PostMapping("/meeting/register")
    public MeetingDTO registerMeeting(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                          @RequestPart("obj") MeetingDTO dto) throws IOException {

        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
            int id = attachmentService.registerAttachments(files);
            dto.setAttachmentGroupId(id);
        }

        return service.registerMeeting(dto);
    }


    // 알림 등록
    @PostMapping("/meeting/alarm/register")
    public int registerCommentAlarm(@RequestBody List<MeetingAlarmDTO> list) {

        return service.registerCommentAlarm(list);
    }

//    회의록 단건 조회
    @GetMapping("/meeting/detail/{id}")
    public Map<String, Object> getMeetingById(@PathVariable Integer id){
        // 결과 담을 그릇
        Map<String, Object> result = new HashMap<>();

        MeetingDTO dto = service.getMeetingById(id);
        result.put("meetingList", dto);

         Integer groupId = dto.getAttachmentGroupId();

         if(groupId != null) {
            List<AttachmentDTO> list = attachmentService.getFileList(groupId);
            result.put("attachmentList", list);
         }

        return result;
    }

//    회의록 수정
        @PutMapping("/meeting/modify")
    public MeetingDTO modifyMeeting(@RequestBody MeetingDTO dto) {
        return service.modifyMeeting(dto);
    }

//    회의록 필터링 조건 조회
    @GetMapping("/meeting/list")
    public Map<String, Object> getFilter ( MeetingDTO dto) {
        return service.getFilter(dto);
    }

//    회의록 페이지네이션
    @GetMapping("/meeting/paging")
    public List<MeetingDTO> getAllMeeting( MeetingDTO dto) {
        return service.getAllMeeting(dto);
    }
}