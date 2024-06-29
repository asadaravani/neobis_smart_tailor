package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;

import java.util.Optional;

public interface ConfirmationCodeService {

    void delete(ConfirmationCode confirmationCode);

    Optional<ConfirmationCode> findByUser(AppUser user);

    Optional<ConfirmationCode> findByUserAndCode(AppUser user, Integer code);

    ConfirmationCode findConfirmationCodeByUser(AppUser user);

    ConfirmationCode generateConfirmationCode(AppUser user);

    void save(ConfirmationCode confirmationCode);
}