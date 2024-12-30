package com.example.service;

import com.example.repository.MessageRepository;

public class MessageService {
    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public void createMessage() {
        // text not blank
        // text not over 255 char
        // postedBy is real account

        // return Message(including ID)
    }

    public void getAllMessages() {
        // return all messages
    }

    public void getMessageById() {
        // return message
    }

    public void updateMessage() {
        // message id already exists
        // text not over 255 char

    }

    public void deleteMessageById() {
        // return number of rows deleted
    }
}
