package com.young.blogusbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class NotificationEmail {

    private String subject;
    private String recipient;
    private String body;
}
