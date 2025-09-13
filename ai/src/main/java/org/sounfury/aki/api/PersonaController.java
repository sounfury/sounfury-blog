package org.sounfury.aki.api;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.prompt.persona.dto.CreatePersonaCommand;
import org.sounfury.aki.application.prompt.persona.dto.DeletePersonaCommand;
import org.sounfury.aki.application.prompt.persona.dto.PersonaDetailResponse;
import org.sounfury.aki.application.prompt.persona.dto.PersonaPageRequest;
import org.sounfury.aki.application.prompt.persona.dto.PersonaPageResponse;
import org.sounfury.aki.application.prompt.persona.dto.UpdatePersonaCommand;
import org.sounfury.aki.application.prompt.persona.service.PersonaApplicationService;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 * 提供角色的增删改API接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/personas")
public class PersonaController {

    private final PersonaApplicationService personaApplicationService;

    /**
     * 获取已启用的角色列表
     */
    @SaIgnore
    @GetMapping("/enabled")
    public Result<PersonaPageResponse> getEnabledPersonas() {
        PersonaPageRequest request = new PersonaPageRequest();
        request.setEnabled(true);
        PersonaPageResponse response = personaApplicationService.getPersonaPage(request);
        return Results.success(response);
    }


    /**
     * 创建角色
     *
     * @param command 创建角色命令
     * @return 创建后的角色信息
     */
    @PostMapping
    public Result<PersonaResponse> createPersona(@Valid @RequestBody CreatePersonaCommand command) {
        log.info("创建角色请求: name={}", command.getName());
        Persona persona = personaApplicationService.createPersona(command);
        PersonaResponse response = PersonaResponse.from(persona);
        return Results.success(response);
    }

    /**
     * 更新角色
     *
     * @param personaId 角色ID
     * @param command   更新角色命令
     * @return 更新后的角色信息
     */
    @PutMapping("/{personaId}")
    public Result<PersonaResponse> updatePersona(
            @PathVariable String personaId,
            @Valid @RequestBody UpdatePersonaCommand command) {
        log.info("更新角色请求: personaId={}", personaId);

        // 设置角色ID
        command.setPersonaId(personaId);

        Persona persona = personaApplicationService.updatePersona(command);
        PersonaResponse response = PersonaResponse.from(persona);
        return Results.success(response);
    }

    /**
     * 删除角色
     *
     * @param personaId 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{personaId}")
    public Result<PersonaResponse> deletePersona(@PathVariable String personaId) {
        log.info("删除角色请求: personaId={}", personaId);
        DeletePersonaCommand command = DeletePersonaCommand.of(personaId);
        personaApplicationService.deletePersona(command);
        return Results.success(PersonaResponse.success("删除角色成功"));
    }

    /**
     * 查询角色详细信息
     *
     * @param personaId 角色ID
     * @return 角色详细信息
     */
    @GetMapping("/{personaId}")
    public Result<PersonaDetailResponse> getPersonaDetail(@PathVariable String personaId) {
        PersonaDetailResponse response = personaApplicationService.getPersonaDetail(personaId);
        return Results.success(response);
    }

    /**
     * 分页查询角色列表
     *
     * @param request 分页查询请求
     * @return 分页查询结果
     */
    @GetMapping
    public Result<PersonaPageResponse> getPersonaPage(@Valid PersonaPageRequest request) {
        PersonaPageResponse response = personaApplicationService.getPersonaPage(request);
        return Results.success(response);
    }

    /**
     * 角色响应DTO
     */
    public static class PersonaResponse {
        private boolean success;
        private String message;
        private String personaId;
        private String personaName;
        private String description;
        private String cardCover;
        private boolean enabled;

        // 静态工厂方法
        public static PersonaResponse from(Persona persona) {
            PersonaResponse response = new PersonaResponse();
            response.success = true;
            response.message = "操作成功";
            response.personaId = persona
                    .getId()
                    .getValue();
            response.personaName = persona.getName();
            response.description = persona.getDescription();
            response.cardCover = persona.getCardCover();
            response.enabled = persona.isEnabled();
            return response;
        }

        public static PersonaResponse success(String message) {
            PersonaResponse response = new PersonaResponse();
            response.success = true;
            response.message = message;
            return response;
        }

        public static PersonaResponse error(String message) {
            PersonaResponse response = new PersonaResponse();
            response.success = false;
            response.message = message;
            return response;
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPersonaId() {
            return personaId;
        }

        public void setPersonaId(String personaId) {
            this.personaId = personaId;
        }

        public String getPersonaName() {
            return personaName;
        }

        public void setPersonaName(String personaName) {
            this.personaName = personaName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getCardCover() {
            return cardCover;
        }

        public void setCardCover(String cardCover) {
            this.cardCover = cardCover;
        }
    }
}
