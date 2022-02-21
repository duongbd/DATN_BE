package vn.nuce.datn_be.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import vn.nuce.datn_be.enity.Message;
import vn.nuce.datn_be.model.dto.MessageDto;
import vn.nuce.datn_be.model.dto.ResponseBody;
import vn.nuce.datn_be.model.form.NotifyCandidateStatus;
import vn.nuce.datn_be.services.MessageService;

import java.util.Date;

@Controller
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RequestMapping("/chat")
@Log4j2
public class ChatController {

    @Autowired
    MessageService messageService;

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return new ResponseEntity<>(ResponseBody.responseBodySuccess(null), HttpStatus.OK);
    }

    @MessageMapping("/say/{roomId}")
    @SendTo({"/chat/{roomId}"})
    public MessageDto sayToRoom(MessageDto messageSend, @DestinationVariable Long roomId){
        log.info("Say to room " + roomId);
        Message message= new Message();
        message.setContent(messageSend.getContent());
        message.setSender(messageSend.getSender());
        message.setReceiver(message.getReceiver());
        message.setTimeCreate(new Date());
        message.setFromIP(message.getFromIP());
        message.setFromMAC(message.getFromMAC());
        message.setRoomFk(roomId);
        messageService.save(message);
        return new MessageDto(message);
    }
}
