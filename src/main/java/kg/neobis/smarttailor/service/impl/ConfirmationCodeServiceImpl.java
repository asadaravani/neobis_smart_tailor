package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.repository.ConfirmationCodeRepository;
import kg.neobis.smarttailor.service.ConfirmationCodeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    ConfirmationCodeRepository confirmationCodeRepository;

    public ConfirmationCode generateConfirmationCode(AppUser user) {

        ConfirmationCode confirmationCode = new ConfirmationCode();
        Random random = new Random();
        Integer code = 1000 + random.nextInt(9999);
        confirmationCode.setCode(code);
        confirmationCode.setUser(user);
        confirmationCodeRepository.save(confirmationCode);

        return confirmationCode;
    }
}