package com.example.authdemo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "group_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessage {
    @Id
    private String id;
    private String groupId;
    private String senderEmail;
    private String content;
    private String filePath;
    private String fileType;
    private Date timestamp;
}
