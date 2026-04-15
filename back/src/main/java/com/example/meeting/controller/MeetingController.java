package com.example.meeting.controller;

import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.service.AttachmentService;
import com.example.meeting.dto.MeetingAlarmDTO;
import com.example.meeting.dto.MeetingDTO;
import com.example.meeting.service.MeetingService;
import com.example.task.dto.TaskReqDtoJJW;
import jakarta.validation.Valid;
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
            int groupId = 0;
            int id = attachmentService.registerAttachments(files, groupId);
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

        Map<String, Object> info = service.getMeetingById(id);
        result.put("meetingList", info);

        MeetingDTO detail = (MeetingDTO) info.get("meetingDetail");
        Integer groupId = detail.getAttachmentGroupId();

         if(groupId != null) {
            List<AttachmentDTO> list = attachmentService.getFileList(groupId);
            result.put("attachmentList", list);
         }

        return result;
    }

//    회의록 수정
        @PutMapping("/meeting/modify")
    public MeetingDTO modifyMeeting(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                    @RequestPart("obj") MeetingDTO dto) throws IOException {

        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
            int groupId = dto.getAttachmentGroupId();
            int id = attachmentService.registerAttachments(files, groupId);
            dto.setAttachmentGroupId(id);
        }

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

    //추천 업무 등록(회의록 생성에서 ai 추천 업무)
    @PostMapping("/tasks/insert")
    public List<MeetingDTO> registerTasks(@Valid @RequestBody TaskReqDtoJJW dto){
        return service.insert(dto);
    };

    // 연결 업무 해제(등록시)
    @DeleteMapping("/meeting/removeConnect")
    public List<MeetingDTO> removeConnect(@RequestBody MeetingDTO dto) {
        return service.removeConnectTask(dto);
    };

    //회의록 업무 등록(회의록 상세)
    @PostMapping("/tasks/registerDetailConnect")
    public List<MeetingDTO> registerDetailConnect(@RequestBody List<MeetingDTO> dto){

        return service.registerDetailConnect(dto);

    };

    // 연결 업무 해제(상세)
    @DeleteMapping("/meeting/removeDetailConnect")
    public List<MeetingDTO> removeDetailConnect(@RequestBody MeetingDTO dto) {
        return service.removeDetailConnect(dto);
    }

    //회의록 업무 등록(회의록 생성 업무 추가)
    @PostMapping("/tasks/registerConnect")
    public List<MeetingDTO> registerConnect(@RequestBody List<MeetingDTO> dto){

        return service.registerDetailConnect(dto);

    };
}