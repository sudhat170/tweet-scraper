package org.webscraper.gpt.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Choice {
    private int index;
    private Message message;
    private String finish_reason;
    // Getters and setters
}
