package org.webscraper.gpt.resp;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OpenAIRequest {

    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;
    private double top_p;
    private double frequency_penalty;
    private double presence_penalty;

}
