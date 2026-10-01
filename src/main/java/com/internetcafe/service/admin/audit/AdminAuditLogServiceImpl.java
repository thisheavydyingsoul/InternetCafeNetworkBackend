package com.internetcafe.service.admin.audit;

import com.internetcafe.entity.Administrator;
import com.internetcafe.entity.Log;
import com.internetcafe.enums.AuditAction;
import com.internetcafe.repository.LogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuditLogServiceImpl implements AdminAuditLogService {

    private final LogRepository logRepository;

    @Override
    @Transactional
    public void record(Administrator administrator, AuditAction action, String message, String ip, String userAgent) {
        Log entry = new Log();
        entry.setAdministrator(administrator);
        entry.setAction(action.name());
        entry.setContents(message);
        entry.setIpAddress(ip);
        entry.setUserAgent(userAgent);
        logRepository.save(entry);
    }
}
