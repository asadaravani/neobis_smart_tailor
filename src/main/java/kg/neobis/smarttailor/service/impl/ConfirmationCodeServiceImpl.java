package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.ConfirmationCode;
import kg.neobis.smarttailor.repository.ConfirmationCodeRepository;
import kg.neobis.smarttailor.service.ConfirmationCodeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    ConfirmationCodeRepository repository;

    @Override
    public void delete(ConfirmationCode confirmationCode) {
        repository.delete(confirmationCode);
    }

    @Override
    public Optional<ConfirmationCode> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    @Override
    public Optional<ConfirmationCode> findByUserAndCode(AppUser user, Integer code) {
        return repository.findByUserAndCode(user, code);
    }

    @Override
    public ConfirmationCode findConfirmationCodeByUser(AppUser user) {
        return repository.findConfirmationCodeByUser(user);
    }

    @Override
    public ConfirmationCode generateConfirmationCode(AppUser user) {
        ConfirmationCode confirmationCode = new ConfirmationCode();
        Random random = new Random();
        Integer code = random.nextInt(1000, 9999);
        confirmationCode.setCode(code);
        confirmationCode.setUser(user);
        repository.save(confirmationCode);
        return confirmationCode;
    }

    @Override
    public void save(ConfirmationCode confirmationCode) {
        repository.save(confirmationCode);
    }
}