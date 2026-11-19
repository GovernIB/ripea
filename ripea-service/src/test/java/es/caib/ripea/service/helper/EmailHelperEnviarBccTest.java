package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Tests unitaris de EmailHelper.enviarBcc: si el servidor de correu rebutja alguna adreça, JavaMail no envia el
 * missatge a ningú i s'ha de tornar a enviar a la resta de destinataris.
 */
@ExtendWith(MockitoExtension.class)
class EmailHelperEnviarBccTest {

	@Mock private JavaMailSender mailSender;
	@InjectMocks private EmailHelper helper;

	@Test
	void senseRebutjosEnviaUnSolCopATotsElsDestinataris() throws Exception {
		MimeMessage message = missatge("a@caib.es", "b@caib.es");
		doNothing().when(mailSender).send(any(MimeMessage.class));

		assertThat(helper.enviarBcc(message, "prova")).containsExactly("a@caib.es", "b@caib.es");
		verify(mailSender, times(1)).send(message);
	}

	@Test
	void ambUnaAdrecaRebutjadaTornaAEnviarALaResta() throws Exception {
		MimeMessage message = missatge("a@caib.es", "cursae21@caib.es", "b@caib.es");
		doThrow(error(adreces(), adreces("a@caib.es", "b@caib.es"), adreces("cursae21@caib.es")))
				.doNothing()
				.when(mailSender).send(any(MimeMessage.class));

		assertThat(helper.enviarBcc(message, "prova")).containsExactly("a@caib.es", "b@caib.es");
		verify(mailSender, times(2)).send(message);
		assertThat(message.getRecipients(Message.RecipientType.BCC)).extracting(Address::toString)
				.containsExactly("a@caib.es", "b@caib.es");
	}

	@Test
	void siTotesLesAdrecesSonRebutjadesPropagaLError() throws Exception {
		MimeMessage message = missatge("cursae21@caib.es");
		MailSendException ex = error(adreces(), adreces(), adreces("cursae21@caib.es"));
		doThrow(ex).when(mailSender).send(any(MimeMessage.class));

		assertThatThrownBy(() -> helper.enviarBcc(message, "prova")).isSameAs(ex);
		verify(mailSender, times(1)).send(message);
	}

	@Test
	void ambEnviamentParcialDelServidorNoTornaAEnviar() throws Exception {
		MimeMessage message = missatge("a@caib.es", "cursae21@caib.es");
		doThrow(error(adreces("a@caib.es"), adreces(), adreces("cursae21@caib.es")))
				.when(mailSender).send(any(MimeMessage.class));

		assertThat(helper.enviarBcc(message, "prova")).containsExactly("a@caib.es");
		verify(mailSender, times(1)).send(message);
	}

	@Test
	void unErrorQueNoEsDAdrecesEsPropaga() throws Exception {
		MimeMessage message = missatge("a@caib.es");
		MailSendException ex = new MailSendException(
				Collections.singletonMap(message, new MessagingException("Connection refused")));
		doThrow(ex).when(mailSender).send(any(MimeMessage.class));

		assertThatThrownBy(() -> helper.enviarBcc(message, "prova")).isSameAs(ex);
		verify(mailSender, times(1)).send(message);
	}

	private static MimeMessage missatge(String... bcc) throws MessagingException {
		MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
		message.setRecipients(Message.RecipientType.BCC, adreces(bcc));
		return message;
	}

	private static MailSendException error(Address[] enviades, Address[] pendents, Address[] rebutjades) {
		return new MailSendException(Collections.singletonMap(
				"missatge",
				new SendFailedException("Invalid Addresses", null, enviades, pendents, rebutjades)));
	}

	private static Address[] adreces(String... adreces) throws MessagingException {
		Address[] resultat = new Address[adreces.length];
		for (int i = 0; i < adreces.length; i++) {
			resultat[i] = new InternetAddress(adreces[i]);
		}
		return resultat;
	}
}
