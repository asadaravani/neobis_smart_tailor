package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
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

    @Override
    public void delete(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.delete(confirmationCode);
    }

    @Override
    public ConfirmationCode findCodeByUser(AppUser user) {
        return confirmationCodeRepository.findByUser(user);
    }

    @Override
    public ConfirmationCode findByUserAndCode(AppUser user, Integer code) {
        return confirmationCodeRepository.findByUserAndCode(user, code)
                .orElseThrow(() -> new ResourceNotFoundException("Confirmation code not found for user with email ".concat(user.getEmail())));
    }

    @Override
    public ConfirmationCode findConfirmationCodeByUser(AppUser user) {
        return confirmationCodeRepository.findConfirmationCodeByUser(user);
    }

    @Override
    public ConfirmationCode generateConfirmationCode(AppUser user) {
        ConfirmationCode confirmationCode = new ConfirmationCode();
        Random random = new Random();
        Integer code = random.nextInt(1000, 9999);
        confirmationCode.setCode(code);
        confirmationCode.setUser(user);
        confirmationCodeRepository.save(confirmationCode);
        return confirmationCode;
    }

    @Override
    public void save(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.save(confirmationCode);
    }
}