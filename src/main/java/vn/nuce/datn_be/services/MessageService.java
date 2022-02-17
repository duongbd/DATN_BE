package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nuce.datn_be.enity.Message;
import vn.nuce.datn_be.repositories.MessageRepository;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;

    public Message save(Message message){
        return messageRepository.save(message);
    }
}
