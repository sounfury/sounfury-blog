package org.sounfury.aki.application.prompt.persona.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.prompt.persona.dto.CreatePersonaCommand;
import org.sounfury.aki.application.prompt.persona.dto.DeletePersonaCommand;
import org.sounfury.aki.application.prompt.persona.dto.PersonaDetailResponse;
import org.sounfury.aki.application.prompt.persona.dto.PersonaPageRequest;
import org.sounfury.aki.application.prompt.persona.dto.PersonaPageResponse;
import org.sounfury.aki.application.prompt.persona.dto.UpdatePersonaCommand;
import org.sounfury.aki.application.shared.event.DomainEventPublisher;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaCard;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.domain.shared.event.DomainEvent;
import org.sounfury.jooq.page.PageRepDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 角色应用服务
 * 负责处理角色相关的业务流程，包括DTO转换、事务管理和事件发布
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonaApplicationService {

    private final CharacterRepository characterRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * 创建角色
     */
    @Transactional
    public Persona createPersona(CreatePersonaCommand command) {
        log.info("创建角色: name={}", command.getName());
        
        try {
            // 检查名称是否已存在
            if (characterRepository.existsByName(command.getName())) {
                throw new IllegalArgumentException("角色名称已存在: " + command.getName());
            }
            
            // 生成角色ID
            String personaId = UUID.randomUUID().toString().replace("-", "");
            
            // 构建角色卡
            PersonaCard card = buildPersonaCard(command.getCard());
            
            // 创建角色实体
            Persona persona = Persona.create(
                PersonaId.of(personaId),
                command.getName(),
                card,
                command.getDescription(),
                command.getCardCover()
            );
            
            // 持久化
            Persona savedPersona = characterRepository.save(persona);
            
            // 发布聚合中的领域事件
            publishDomainEvents(savedPersona);
            
            log.info("角色创建成功: id={}, name={}", savedPersona.getId().getValue(), savedPersona.getName());
            return savedPersona;
            
        } catch (Exception e) {
            log.error("创建角色失败: name={}", command.getName(), e);
            throw new RuntimeException("创建角色失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新角色
     */
    @Transactional
    public Persona updatePersona(UpdatePersonaCommand command) {
        log.info("更新角色: personaId={}", command.getPersonaId());
        
        try {
            // 查找现有角色
            Persona existingPersona = characterRepository
                .findPersonaById(PersonaId.of(command.getPersonaId()))
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + command.getPersonaId()));
            
            Persona updatedPersona = existingPersona;
            
            // 根据变更类型执行相应的业务操作
            if (command.hasNameChange()) {
                // 检查新名称是否与其他角色冲突
                if (!existingPersona.getName().equals(command.getName()) && 
                    characterRepository.existsByName(command.getName())) {
                    throw new IllegalArgumentException("角色名称已存在: " + command.getName());
                }
                updatedPersona = updatedPersona.updateName(command.getName());
                log.debug("更新角色名称: personaId={}, newName={}", command.getPersonaId(), command.getName());
            }
            
            if (command.hasDescriptionChange()) {
                updatedPersona = updatedPersona.updateDescription(command.getDescription());
                log.debug("更新角色描述: personaId={}", command.getPersonaId());
            }
            
            if (command.hasCardChange()) {
                PersonaCard newCard = buildPersonaCard(command.getCard());
                updatedPersona = updatedPersona.updateCard(newCard);
                log.debug("更新角色卡: personaId={}", command.getPersonaId());
            }
            
            if (command.hasEnabledChange()) {
                updatedPersona = command.getEnabled() ? 
                    updatedPersona.enable() : 
                    updatedPersona.disable();
                log.debug("更新启用状态: personaId={}, enabled={}", command.getPersonaId(), command.getEnabled());
            }
            
            if (command.hasWorldBookChange()) {
                updatedPersona = updatedPersona.updateWorldBook(command.getWorldBookId());
                log.debug("更新世界书: personaId={}, worldBookId={}", command.getPersonaId(), command.getWorldBookId());
            }
            
            if (command.hasCardCoverChange()) {
                updatedPersona = updatedPersona.updateCardCover(command.getCardCover());
                log.debug("更新角色卡封面: personaId={}, cardCover={}", command.getPersonaId(), command.getCardCover());
            }
            
            // 持久化
            Persona savedPersona = characterRepository.save(updatedPersona);
            
            // 发布聚合中的领域事件
            publishDomainEvents(savedPersona);
            
            log.info("角色更新成功: personaId={}", command.getPersonaId());
            return savedPersona;
            
        } catch (Exception e) {
            log.error("更新角色失败: personaId={}", command.getPersonaId(), e);
            throw new RuntimeException("更新角色失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deletePersona(DeletePersonaCommand command) {
        log.info("删除角色: personaId={}", command.getPersonaId());
        
        try {
            PersonaId personaId = PersonaId.of(command.getPersonaId());
            
            // 检查角色是否存在
            Persona persona = characterRepository
                .findPersonaById(personaId)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + command.getPersonaId()));
            
            // 标记删除并记录事件
            Persona deletedPersona = persona.markDeleted();
            
            // 发布删除前的领域事件
            publishDomainEvents(deletedPersona);
            
            // 执行物理删除
            characterRepository.deleteById(personaId);
            
            log.info("角色删除成功: personaId={}", command.getPersonaId());
            
        } catch (Exception e) {
            log.error("删除角色失败: personaId={}", command.getPersonaId(), e);
            throw new RuntimeException("删除角色失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建角色卡
     */
    private PersonaCard buildPersonaCard(UpdatePersonaCommand.PersonaCardDto cardDto) {
        if (cardDto == null) {
            throw new IllegalArgumentException("角色卡信息不能为空");
        }
        
        return PersonaCard.of(
            cardDto.getCharName(),
            cardDto.getCharPersona(),
            cardDto.getWorldScenario(),
            cardDto.getCharGreeting(),
            cardDto.getExampleDialogue()
        );
    }
    
    /**
     * 构建角色卡（创建时）
     */
    private PersonaCard buildPersonaCard(CreatePersonaCommand.PersonaCardDto cardDto) {
        if (cardDto == null) {
            throw new IllegalArgumentException("角色卡信息不能为空");
        }
        
        return PersonaCard.of(
            cardDto.getCharName(),
            cardDto.getCharPersona(),
            cardDto.getWorldScenario(),
            cardDto.getCharGreeting(),
            cardDto.getExampleDialogue()
        );
    }

    /**
     * 获取角色详细信息
     */
    public PersonaDetailResponse getPersonaDetail(String personaId) {
        log.info("查询角色详细信息: personaId={}", personaId);
        
        try {
            Persona persona = characterRepository
                .findPersonaDetailById(PersonaId.of(personaId))
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + personaId));
            
            return PersonaDetailResponse.from(persona);
            
        } catch (Exception e) {
            log.error("查询角色详细信息失败: personaId={}", personaId, e);
            throw new RuntimeException("查询角色详细信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 分页查询角色列表
     */
    public PersonaPageResponse getPersonaPage(PersonaPageRequest request) {
        log.info("分页查询角色列表: page={}, size={}, keyword={}", 
                request.getPage(), request.getSize(), request.getKeyword());
        
        try {
            PageRepDto<List<Persona>> pageResult = characterRepository.findPersonaPage(request);
            return PersonaPageResponse.from(pageResult);
            
        } catch (Exception e) {
            log.error("分页查询角色列表失败: request={}", request, e);
            throw new RuntimeException("分页查询角色列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 发布聚合中的领域事件
     */
    private void publishDomainEvents(Persona persona) {
        try {
            for (DomainEvent event : persona.getDomainEvents()) {
                eventPublisher.publish(event);
                log.debug("发布领域事件: eventType={}, personaId={}", 
                         event.getEventType(), 
                         persona.getId().getValue());
            }
            // 清除已发布的事件
            persona.clearDomainEvents();
        } catch (Exception e) {
            log.error("发布领域事件失败: personaId={}", persona.getId().getValue(), e);
            throw new RuntimeException("发布领域事件失败", e);
        }
    }
}
