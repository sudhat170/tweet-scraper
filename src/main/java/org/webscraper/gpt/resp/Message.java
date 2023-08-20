package org.webscraper.gpt.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Message {
    String role;
    String content;
}
