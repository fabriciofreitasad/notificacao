package com.targetcar.notificacao.business;

import com.targetcar.notificacao.business.dto.TarefasDTO;
import com.targetcar.notificacao.infrastructere.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${envio.mail.remetente}")
    private String remetente;

    @Value("${envio.mail.nomeRemetente}")
    private String nomeRemetente;

    /**
     * Envia um e-mail com a notificação da tarefa.
     * @param dto DTO com as informações da tarefa.
     */
    public void enviaEmail(TarefasDTO dto) {
        // Verificação de validade do DTO
        if (dto == null) {
            throw new EmailException("O DTO está nulo.");
        }

        if (dto.getEmailUsuario() == null || dto.getNomeTarefa() == null) {
            throw new EmailException("Dados da tarefa inválidos ou incompletos.");
        }

        // Verificar o conteúdo do DTO para depuração
        System.out.println("DTO recebido para envio de e-mail: " + dto);

        try {
            // Criação e envio do e-mail
            MimeMessage message = createMimeMessage(dto);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar o e-mail para: " + dto.getEmailUsuario(), e);
        }
    }

    /**
     * Cria a mensagem MIME com as informações do e-mail.
     * @param dto DTO com as informações da tarefa.
     * @return MimeMessage configurado.
     * @throws MessagingException Se ocorrer um erro ao configurar a mensagem.
     * @throws UnsupportedEncodingException Se a codificação de caracteres não for suportada.
     */
    private MimeMessage createMimeMessage(TarefasDTO dto) throws MessagingException, UnsupportedEncodingException {
        // Criação da mensagem MIME
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        // Definindo o remetente e o destinatário
        mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));
        mimeMessageHelper.setTo(InternetAddress.parse(dto.getEmailUsuario()));
        mimeMessageHelper.setSubject("Notificação de Tarefa: " + dto.getNomeTarefa());

        // Construção do corpo do e-mail
        String corpoEmail = buildEmailBody(dto);
        mimeMessageHelper.setText(corpoEmail, true);

        return message;
    }

    /**
     * Constrói o corpo do e-mail usando o template Thymeleaf.
     * @param dto DTO com as informações da tarefa.
     * @return Corpo do e-mail em formato HTML.
     */
    private String buildEmailBody(TarefasDTO dto) {
        // Criando o contexto para o Thymeleaf
        Context context = new Context();
        context.setVariable("nomeTarefa", dto.getNomeTarefa());
        context.setVariable("dataEvento", dto.getDataEvento());
        context.setVariable("descricao", dto.getDescricacao());
        context.setVariable("valor", dto.getValor());
        context.setVariable("dataVencimento", dto.getDataVencimento());
        context.setVariable("linkPix", dto.getLinkPix());

        // Verificar o conteúdo do contexto para depuração
        System.out.println("Contexto criado para o Thymeleaf: " + context);

        // Gerando o template do corpo do e-mail
        return templateEngine.process("notificacao", context);
    }
}
