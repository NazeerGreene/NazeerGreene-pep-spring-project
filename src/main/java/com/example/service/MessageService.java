package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.repository.MessageRepository;

import com.example.entity.Message;

@Service
public class MessageService {
    private final MessageRepository repository;

    @Autowired
    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public Optional<Message> createMessage(Message newMessage) {
        if (newMessage == null) {
            throw new IllegalArgumentException("newMessage cannot be null");
        }

        if (!isValidMessageText(newMessage.getMessageText())) {
            return Optional.empty();
        }

        // new message should be valid by this point
        newMessage.setMessageId(null); // no id should be present
        Message savedMessage = repository.save(newMessage);

        return Optional.of(savedMessage);
    }

    public List<Message> getAllMessages() {
        return (ArrayList<Message>) repository.findAll();
    }

    public Optional<Message> getMessageById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        return repository.findById(id);
    }

    public int updateMessage(Message modifiedMessage) {
        if (modifiedMessage == null) {
            throw new IllegalArgumentException("modifiedMessage cannot be null");
        }

        if (!isValidMessageText(modifiedMessage.getMessageText())) {
            System.out.println("failed: " + modifiedMessage.getMessageText());
            return 0;
        }

        Integer id = modifiedMessage.getMessageId();

        if (id == null || getMessageById(id).isEmpty()) {
            return 0;
        }

        // modified message should be valid by this point
        modifiedMessage.setPostedBy(null);               // no need to modify
        modifiedMessage.setTimePostedEpoch(null); // no need to modify
        Message updatedMessage = repository.save(modifiedMessage);

        if (updatedMessage.getMessageText().equals(modifiedMessage.getMessageText())) {
            System.out.println("message: " + updatedMessage.getMessageText());
            return 1;
        }
        System.out.println("message: " + updatedMessage.getMessageText());

        return 0;
    }

    public int deleteMessageById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (getMessageById(id).isPresent()) {
            repository.deleteById(id);
            return 1;
        }
        return 0;
    }

    public List<Message> getMessagesFromUserId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        return (ArrayList<Message>) repository.findByPostedBy(id);
    }

    private boolean isValidMessageText(String message) {
        return message != null && !message.isBlank() && message.length() <= 255;
    }
}
