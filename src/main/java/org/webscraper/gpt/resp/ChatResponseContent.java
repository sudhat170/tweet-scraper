package org.webscraper.gpt.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponseContent {
    private int vendor = 0;
    private int quality = 0;
    private int response = 0;
}
