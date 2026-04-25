package com.example.webhd.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private static final String API_KEY = "23fc28f4-f0bd-468c-96cc-dac4eac03145";
    private static final String MODEL_ID = "doubao-seed-1-8-251228";
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";
//    private static final String API_URL = "https://ark.cn-guangzhou.volces.com/api/v3/chat/completions";

    // ================== 1. 原来的美食解说接口（保留） ==================
    @GetMapping("/explain")
    public String aiExplain(@RequestParam String foodName) {
        System.out.println("请求生成美食解说：" + foodName);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Authorization", "Bearer " + API_KEY);

            String prompt = String.format(
                    "你是非遗美食文化专家，请为%s撰写专业解说：\n" +
                            "1.历史渊源；2.非遗级别与传承；3.制作工艺；4.文化价值。\n" +
                            "格式清晰，200字左右，适合网页展示。",
                    foodName
            );

            JSONObject body = new JSONObject();
            body.put("model", MODEL_ID);
            body.put("messages", new JSONObject[]{
                    new JSONObject()
                            .fluentPut("role", "user")
                            .fluentPut("content", prompt)
            });
            body.put("temperature", 0.9f);
            body.put("max_tokens", 384);

            StringEntity entity = new StringEntity(body.toJSONString(), "UTF-8");
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity resEntity = response.getEntity();
                String result = EntityUtils.toString(resEntity, "UTF-8");
                JSONObject json = JSON.parseObject(result);

                if (json.containsKey("error")) {
                    return "AI服务调用失败：" + json.getJSONObject("error").getString("message");
                }

                return json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "AI解说生成失败：" + e.getMessage();
        }
    }

    // ================== 2. 新增：AI 问答聊天接口 ==================
    @GetMapping("/chat")
    public String aiChat(@RequestParam String prompt) {
        System.out.println("用户提问：" + prompt);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Authorization", "Bearer " + API_KEY);

            // 构造对话请求（自由问答）
            JSONObject body = new JSONObject();
            body.put("model", MODEL_ID);
            body.put("messages", new JSONObject[]{
                    new JSONObject()
                            .fluentPut("role", "user")
                            .fluentPut("content", prompt)
            });
            body.put("temperature", 0.9f);
            body.put("max_tokens", 384);

            StringEntity entity = new StringEntity(body.toJSONString(), "UTF-8");
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity resEntity = response.getEntity();
                String result = EntityUtils.toString(resEntity, "UTF-8");
                JSONObject json = JSON.parseObject(result);

                if (json.containsKey("error")) {
                    return "AI回答失败：" + json.getJSONObject("error").getString("message");
                }

                return json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务异常：" + e.getMessage();
        }
    }
}