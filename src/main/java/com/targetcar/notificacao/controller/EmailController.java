package com.targetcar.notificacao.controller;

import com.targetcar.notificacao.business.EmailService;
import com.targetcar.notificacao.business.dto.TarefasDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Void> enviarEmail(@RequestBody TarefasDTO dto){
       emailService.enviaEmail(dto);
        return ResponseEntity.ok().build();
    }
}
