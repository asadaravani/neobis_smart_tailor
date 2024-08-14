package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.InvitationToken;
import kg.neobis.smarttailor.entity.Organization;
import kg.neobis.smarttailor.entity.Position;

public interface InvitationTokenService {

    void delete(InvitationToken invitationToken);

    void deleteExpiredTokens();

    InvitationToken findByToken(String token);

    InvitationToken findByUser(AppUser user);

    InvitationToken generateInvitationToken(AppUser user, Organization organization, Position position);
}