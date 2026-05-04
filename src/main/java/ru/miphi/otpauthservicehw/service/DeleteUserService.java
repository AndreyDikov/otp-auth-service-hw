package ru.miphi.otpauthservicehw.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.DeleteUserRepository;

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
        deleteUserRepository.findUserRoleById(userId)
                .filter(role -> role != ADMIN)
                .ifPresentOrElse(
                        role -> deleteUserRepository.deleteUser(userId),
                        () -> {
                            if (deleteUserRepository.findUserRoleById(userId)
                                    .filter(role -> role == ADMIN)
                                    .isPresent()) {
                                throw BusinessLogicException.of(ADMIN_DELETE_FORBIDDEN);
                            }
                        }
                );
    }

}
