package com.aioveu.boot.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用户在线状态服务
 * 负责维护用户的在线状态和相关统计
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
public class UserOnlineService {

    // 在线用户映射表，key为用户名，value为用户在线信息
    private final Map<String, UserOnlineInfo> onlineUsers = new ConcurrentHashMap<>();
    
    private SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserOnlineService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 用户上线
     *
     * @param username  用户名
     * @param sessionId WebSocket会话ID（可选）
     */
    public void userConnected(String username, String sessionId) {
        // 生成会话ID（如果未提供）
        String actualSessionId = sessionId != null ? sessionId : "session-" + System.nanoTime();
        UserOnlineInfo info = new UserOnlineInfo(username, actualSessionId, System.currentTimeMillis());
        onlineUsers.put(username, info);
        log.info("用户[{}]上线，当前在线用户数：{}", username, onlineUsers.size());
        
        // 通知在线用户状态变更
        notifyOnlineUsersChange();
    }

    /**
     * 用户下线
     *
     * @param username 用户名
     */
    public void userDisconnected(String username) {
        onlineUsers.remove(username);
        log.info("用户[{}]下线，当前在线用户数：{}", username, onlineUsers.size());
        
        // 通知在线用户状态变更
        notifyOnlineUsersChange();
    }

    /**
     * 获取在线用户列表
     *
     * @return 在线用户名列表
     */
    public List<UserOnlineDTO> getOnlineUsers() {
        return onlineUsers.values().stream()
                .map(info -> new UserOnlineDTO(info.getUsername(), info.getLoginTime()))
                .collect(Collectors.toList());
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * 检查用户是否在线
     *
     * @param username 用户名
     * @return 是否在线
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    /**
     * 通知所有客户端在线用户变更
     */
    private void notifyOnlineUsersChange() {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送在线用户数量");
            return;
        }
        
        // 发送简化版数据（仅数量）
        sendOnlineUserCount();
    }
    
    /**
     * 发送在线用户数量（简化版，不包含用户详情）
     */
    private void sendOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("消息模板尚未初始化，无法发送在线用户数量");
            return;
        }
        
        try {
            // 直接发送数量，更轻量
            int count = onlineUsers.size();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("已发送在线用户数量: {}", count);
        } catch (Exception e) {
            log.error("发送在线用户数量失败", e);
        }
    }

    /**
     * 用户在线信息
     */
    @Data
    private static class UserOnlineInfo {
        private final String username;
        private final String sessionId;
        private final long loginTime;
    }

    /**
     * 用户在线DTO（用于返回给前端）
     */
    @Data
    public static class UserOnlineDTO {
        private final String username;
        private final long loginTime;
    }

    /**
     * 在线用户变更事件
     */
    @Data
    private static class OnlineUsersChangeEvent {
        private String type;
        private int count;
        private List<UserOnlineDTO> users;
        private long timestamp;
    }
} 
