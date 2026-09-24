package com.internetcafe.service;

import com.internetcafe.entity.Administrator;
import com.internetcafe.enums.AuditAction;

public interface AdminAuditLogService {
    void record(Administrator administrator, AuditAction action, String message, String ip, String userAgent);
}
