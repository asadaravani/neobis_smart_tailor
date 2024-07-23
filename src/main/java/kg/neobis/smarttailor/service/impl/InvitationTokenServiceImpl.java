package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.InvitationToken;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.Position;
import kg.neobis.smarttailor.repository.InvitationTokenRepository;
import kg.neobis.smarttailor.service.InvitationTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvitationTokenServiceImpl implements InvitationTokenService {

    InvitationTokenRepository invitationTokenRepository;

    @Override
    public void delete(InvitationToken invitationToken) {
        invitationTokenRepository.delete(invitationToken);
    }

    @Override
    public InvitationToken findByToken(String token) {
        return invitationTokenRepository.findByToken(token);
    }

    @Override
    public InvitationToken generateInvitationToken(AppUser user, Organization organization, Position position) {

        InvitationToken invitationToken = new InvitationToken();
        String token = UUID.randomUUID().toString();
        invitationToken.setToken(token);
        invitationToken.setUser(user);
        invitationToken.setOrganization(organization);
        invitationToken.setPosition(position);
        invitationTokenRepository.save(invitationToken);

        return invitationToken;
    }
}