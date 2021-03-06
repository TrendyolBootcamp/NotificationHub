package org.kodluyoruz.trendyol.business.notification;

import org.kodluyoruz.trendyol.business.notification.abstraction.FixedNotificationSender;
import org.kodluyoruz.trendyol.business.validation.MessageContentValidation;
import org.kodluyoruz.trendyol.constants.ErrorMessage;
import org.kodluyoruz.trendyol.datastructures.EmailFixedPackage;
import org.kodluyoruz.trendyol.exceptions.InvalidMessageContentException;
import org.kodluyoruz.trendyol.models.Company;
import org.kodluyoruz.trendyol.models.Email;
import org.kodluyoruz.trendyol.models.dtos.NotificationSendDTO;

public class EmailFixedNotificationSender implements FixedNotificationSender {
    @Override
    public void sendNotification(NotificationSendDTO notificationSendDTO) {
        Email email = (Email) notificationSendDTO.getMessage();
        Company company = notificationSendDTO.getCompany();

        boolean validContent = MessageContentValidation.checkMessageContent(email);

        if (!validContent) throw new InvalidMessageContentException(ErrorMessage.invalidMessageContent(company.getLanguage()));

        if (company.getEmailPackage().limit <= 0) {
            System.out.printf("\n" + company.getName() + " - exceeded Email limit (FixedPackage)" +
                    " - current invoice : %.2f \n", company.getInvoice());

            defineExtraPackage(company);
        }
        company.getEmailPackage().limit--;

        System.out.println(company.getName() +
                " - sent Email (FixedPackage) -> " + notificationSendDTO.getUserName() +
                " - subject : " + email.getSubject() + " - content : " + email.getContent() +
                " - remaining limit : " + company.getEmailPackage().limit);
    }

    @Override
    public void defineExtraPackage(Company company) {
        EmailFixedPackage emailFixedPackage = (EmailFixedPackage) company.getEmailPackage();

        company.getEmailPackage().limit = emailFixedPackage.limitExcessExtraLimit;
        company.setInvoice(company.getInvoice() + emailFixedPackage.limitExcessPackagePrice);

        System.out.printf(company.getName() + " - defining extra Email package (FixedPackage)" +
                " - new Email package limit : " + emailFixedPackage.limitExcessExtraLimit +
                " - new invoice : %.2f \n\n", company.getInvoice());
    }
}
