package com.example.controller;

import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

public class SocialMediaMessageController {

    private final MessageService service;

    SocialMediaMessageController(MessageService service) {
        this.service = service;
    }

    public void createMessage() {
        // return 400 if bad
    }

    public void getAllMessages() {

    }

    public void getMessageById() {
        // always 200; empty if no message
    }

    public void updateMessage() {
        //. 400 on bad; updated rows otherwise
        
    }

    public void deleteMessage() {
        // always 200; either number of rows deleted or empty
    }

    public void getMessagesFromUserId() {
        // always 200; empty or list
    }

}
