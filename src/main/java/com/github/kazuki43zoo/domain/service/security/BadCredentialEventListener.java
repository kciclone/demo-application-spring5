package com.github.kazuki43zoo.domain.service.security;

import com.github.kazuki43zoo.domain.model.account.AccountAuthenticationHistory;
import com.github.kazuki43zoo.domain.model.account.AuthenticationType;
import com.github.kazuki43zoo.domain.service.password.PasswordSharedService;
import org.dozer.Mapper;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Transactional
@Component
public final class BadCredentialEventListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Inject
    AuthenticationSharedService authenticationSharedService;

    @Inject
    PasswordSharedService passwordSharedService;

    @Inject
    Mapper beanMapper;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String failedAccountId = event.getAuthentication().getName();

        passwordSharedService.countUpPasswordFailureCount(failedAccountId);

        AccountAuthenticationHistory authenticationHistory =
                beanMapper.map(event.getAuthentication().getDetails(), AccountAuthenticationHistory.class);
        authenticationSharedService.createAuthenticationFailureHistory(
                failedAccountId, authenticationHistory, AuthenticationType.LOGIN, event.getException().getMessage());
    }

}