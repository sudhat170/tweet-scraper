package org.webscraper.gpt.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Usage {
    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;
}