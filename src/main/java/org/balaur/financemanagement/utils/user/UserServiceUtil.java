package org.balaur.financemanagement.utils.user;

import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.model.user.UserGroup;

import java.util.List;

@Slf4j
public class UserServiceUtil {
    public static List<String> getUserGroupCodes(User user) {
        return user.getUserGroups().stream()
                .map(UserGroup::getCode)
                .toList();
    }
}
