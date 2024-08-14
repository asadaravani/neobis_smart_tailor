package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;

public interface ConfirmationCodeService {

    void delete(ConfirmationCode confirmationCode);

    void deleteExpiredCodes();

    ConfirmationCode findByUserAndCode(AppUser user, Integer code);

    ConfirmationCode findConfirmationCodeByUser(AppUser user);

    ConfirmationCode generateConfirmationCode(AppUser user);

    void save(ConfirmationCode confirmationCode);
}