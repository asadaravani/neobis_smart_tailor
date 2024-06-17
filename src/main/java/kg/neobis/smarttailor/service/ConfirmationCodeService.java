package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;

public interface ConfirmationCodeService {
    ConfirmationCode generateConfirmationCode(AppUser user);
}
