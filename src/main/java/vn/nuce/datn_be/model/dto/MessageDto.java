package vn.nuce.datn_be.model.dto;

import lombok.Getter;
import lombok.Setter;
import vn.nuce.datn_be.enity.Message;
import vn.nuce.datn_be.utils.TimeUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MessageDto {
    @NotBlank
    String content;
    @NotBlank
    String time;
    @NotBlank
    String date;
    @NotBlank
    String sender;
    @NotBlank
    String receiver;
    String fromIP;
    String fromMAC;

    public MessageDto(){}

    public MessageDto(Message message) {
        this.content = message.getContent();
        this.receiver = message.getReceiver();
        this.time = TimeUtils.DateToString(message.getTimeCreate(), "HH:mm:ss");
        this.date = TimeUtils.DateToString(message.getTimeCreate(), "yyyy-MM-dd");
        this.sender = message.getSender();
    }
}
