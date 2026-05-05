package ru.miphi.otpauthservicehw.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miphi.otpauthservicehw.enums.UserRole;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.DeleteUserRepository;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.UserRole.ADMIN;
import static ru.miphi.otpauthservicehw.exception.ErrorType.ADMIN_DELETE_FORBIDDEN;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DeleteUserService {

    DeleteUserRepository deleteUserRepository;

    @Transactional
    public void deleteUser(Long userId) {
        Optional<UserRole> userRole = deleteUserRepository.findUserRoleById(userId);

        if (userRole.isEmpty()) {
            return;
        }

        if (userRole.get() == ADMIN) {
            throw BusinessLogicException.of(ADMIN_DELETE_FORBIDDEN);
        }

        deleteUserRepository.deleteUser(userId);
    }

}
