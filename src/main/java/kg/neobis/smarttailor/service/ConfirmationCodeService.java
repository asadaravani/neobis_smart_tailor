package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;

public interface ConfirmationCodeService {

    void delete(ConfirmationCode confirmationCode);

    ConfirmationCode findByUserAndCode(AppUser user, Integer code);

    ConfirmationCode findCodeByUser(AppUser user);

    ConfirmationCode findConfirmationCodeByUser(AppUser user);

    ConfirmationCode generateConfirmationCode(AppUser user);

    void save(ConfirmationCode confirmationCode);
}