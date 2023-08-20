package org.webscraper.gpt;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.webscraper.gpt.resp.ChatCompletionResponse;
import org.webscraper.gpt.resp.ChatResponseContent;
import org.webscraper.gpt.resp.Message;
import org.webscraper.gpt.resp.OpenAIRequest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatGPT {

    private static int vendor = 0;
    private static int quality = 0;
    private static int responsec = 0;

    public static void main(String[] args) throws IOException {
        int chunkSize = 200;
        int startLine = 0;
        int endLine = startLine + chunkSize;

        while (endLine <= 4473) {
            String message = readLines(startLine, endLine);
            System.out.println("End line :" + endLine);
            callAPI(message);
            // Move to the next chunk
            startLine = endLine + 1; // Start from the next line
            endLine = startLine + chunkSize - 1; // Read the next chunkSize lines
        }

        // In case the last chunk is smaller than chunkSize
        if (startLine <= 4473) {
            System.out.println("End line :" + 4552);
            String message = readLines(startLine, 4552);
            callAPI(message);
        }

    }

    private static void callAPI(String message) {
        if (message.isBlank())
            return;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer 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");

        OpenAIRequest requestBody = constructRequestBody(message);

        try {
            StringEntity entity = new StringEntity(new Gson().toJson(requestBody));
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity);

                ChatResponseContent responseContent = extractResponseContent(responseString);
                vendor += responseContent.getVendor();
                quality += responseContent.getQuality();
                responsec += responseContent.getResponse();
                System.out.println("Vendor: " + vendor);
                System.out.println("Quality: " + quality);
                System.out.println("Response: " + responsec);
                System.out.println("");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // read from file
    private static String readLines(int startLine, int endLine) throws IOException {
        StringBuilder message = new StringBuilder();
        String filePath = "output.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int lineCount = 0; // Initialize lineCount to track the line number
            String line;

            // Read and process lines until endLine
            while ((line = reader.readLine()) != null) {
                if (lineCount >= startLine) {
                    message.append(" ").append(line);
                }

                lineCount++;

                if (lineCount > endLine) {
                    break; // Exit the loop when endLine is reached
                }
            }
        }

        return message.toString();
    }

    // get values
    private static ChatResponseContent extractResponseContent(String jsonResponse) {
//        System.out.println(jsonResponse);
        ChatCompletionResponse chatResponse = new Gson().fromJson(jsonResponse, ChatCompletionResponse.class);
        String content = chatResponse.getChoices().get(0).getMessage().getContent();
        return new Gson().fromJson(content, ChatResponseContent.class);
    }


    // create request
    private static OpenAIRequest constructRequestBody(String message) {
        OpenAIRequest request = new OpenAIRequest();
        request.setModel("gpt-3.5-turbo");

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "you are given tweets in the form of raw text format. These are complaints from customers of Asian paints. Your job is to categorize these into these 1. vendor (issue is from the dealer/vendor side) 2. quality (quality issue)3. response (late/no response or no actions on complaints ) and return the count of issues in each category, based on the issue faced by the consumer. If the tweet seems to be from the Asian paints or there are no issues then do not count it, if the tweets seem to be a part of the same conversation then count it as 1. Always return the response in this format - {category: count} and if a category count is 0 then return 0 for it"));
        messages.add(new Message("user", message));

        request.setMessages(messages);
        request.setTemperature(1.0);
        request.setMax_tokens(256);
        request.setTop_p(1.0);
        request.setFrequency_penalty(0.0);
        request.setPresence_penalty(0.0);

        return request;
    }
}