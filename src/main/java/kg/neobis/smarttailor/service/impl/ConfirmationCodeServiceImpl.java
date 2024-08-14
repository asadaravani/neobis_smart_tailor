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

import java.time.LocalDateTime;
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
    public void deleteExpiredCodes() {
        confirmationCodeRepository.deleteByExpirationTimeBefore(LocalDateTime.now());
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
        Random random = new Random();
        Integer code = random.nextInt(1000, 9999);

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code(code)
                .user(user)
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .build();
        confirmationCodeRepository.save(confirmationCode);

        return confirmationCode;
    }

    @Override
    public void save(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.save(confirmationCode);
    }
}