package org.sounfury.aki.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaCard;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.aki.domain.prompt.repository.CharacterRepository;
import org.sounfury.aki.jooq.tables.daos.PersonaDao;
import org.sounfury.aki.jooq.tables.daos.PersonaCardDao;
import org.sounfury.aki.jooq.tables.pojos.PersonaPojo;
import org.sounfury.aki.jooq.tables.pojos.PersonaCardPojo;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.sounfury.aki.jooq.tables.Persona.PERSONA;
import static org.sounfury.aki.jooq.tables.PersonaCard.PERSONA_CARD;

/**
 * JOOQ实现的角色仓储
 */
@Slf4j
@Repository
public class JdbcCharacterRepository extends PersonaDao implements CharacterRepository {

    private final PersonaCardDao personaCardDao;


    public JdbcCharacterRepository(Configuration configuration) {
        super(configuration);
        this.personaCardDao = new PersonaCardDao(configuration);
    }

    @Override
    public Optional<Persona> findPersonaById(PersonaId id) {
        if (id == null) {
            return Optional.empty();
        }

        try {
            PersonaPojo personaPojo = fetchOneById(id.getValue());
            if (personaPojo == null) {
                return Optional.empty();
            }

            PersonaCardPojo cardPojo = personaCardDao.ctx()
                    .selectFrom(PERSONA_CARD)
                    .where(PERSONA_CARD.PERSONA_ID.eq(id.getValue()))
                    .fetchOneInto(PersonaCardPojo.class);

            return Optional.of(toDomain(personaPojo, cardPojo));
        } catch (Exception e) {
            log.error("根据ID查找角色失败: {}", id.getValue(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Persona> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            PersonaPojo personaPojo = ctx()
                    .selectFrom(PERSONA)
                    .where(PERSONA.NAME.eq(name.trim()))
                    .fetchOneInto(PersonaPojo.class);

            if (personaPojo == null) {
                return Optional.empty();
            }

            PersonaCardPojo cardPojo = personaCardDao.ctx()
                    .selectFrom(PERSONA_CARD)
                    .where(PERSONA_CARD.PERSONA_ID.eq(personaPojo.getId()))
                    .fetchOneInto(PersonaCardPojo.class);

            return Optional.of(toDomain(personaPojo, cardPojo));
        } catch (Exception e) {
            log.error("根据名称查找角色失败: {}", name, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Persona> findAllEnabled() {
        try {
            List<PersonaPojo> personaPojos = ctx()
                    .selectFrom(PERSONA)
                    .where(PERSONA.ENABLED.eq((byte) 1))
                    .fetchInto(PersonaPojo.class);

            return personaPojos.stream()
                    .map(this::loadPersonaWithCard)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找所有启用角色失败", e);
            return List.of();
        }
    }

    @Override
    public List<Persona> findAllPersona() {
        try {
            List<PersonaPojo> personaPojos = ctx()
                    .selectFrom(PERSONA)
                    .fetchInto(PersonaPojo.class);

            return personaPojos.stream()
                    .map(this::loadPersonaWithCard)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找所有角色失败", e);
            return List.of();
        }
    }

    @Override
    public Persona save(Persona persona) {
        try {
            PersonaPojo personaPojo = fromDomainPersona(persona);
            PersonaCardPojo cardPojo = fromDomainCard(persona);

            // 保存或更新Persona
            if (existsById(persona.getId())) {
                update(personaPojo);
                log.debug("更新角色: id={}, name={}", persona.getId().getValue(), persona.getName());
            } else {
                insert(personaPojo);
                log.debug("插入角色: id={}, name={}", persona.getId().getValue(), persona.getName());
            }

            // 保存或更新PersonaCard
            boolean cardExists = personaCardDao.ctx()
                    .fetchExists(
                            personaCardDao.ctx().selectOne()
                                    .from(PERSONA_CARD)
                                    .where(PERSONA_CARD.PERSONA_ID.eq(persona.getId().getValue()))
                    );

            if (cardExists) {
                personaCardDao.update(cardPojo);
                log.debug("更新角色卡: personaId={}", persona.getId().getValue());
            } else {
                personaCardDao.insert(cardPojo);
                log.debug("插入角色卡: personaId={}", persona.getId().getValue());
            }

            return persona;
        } catch (Exception e) {
            log.error("保存角色失败: id={}", persona.getId().getValue(), e);
            throw new RuntimeException("保存角色失败", e);
        }
    }

    @Override
    public void deleteById(PersonaId id) {
        if (id == null) {
            return;
        }

        try {
            // 先删除角色卡（外键约束）
            personaCardDao.ctx()
                    .deleteFrom(PERSONA_CARD)
                    .where(PERSONA_CARD.PERSONA_ID.eq(id.getValue()))
                    .execute();

            // 再删除角色
            ctx()
                    .deleteFrom(PERSONA)
                    .where(PERSONA.ID.eq(id.getValue()))
                    .execute();

            log.debug("删除角色及角色卡: id={}", id.getValue());
        } catch (Exception e) {
            log.error("删除角色失败: id={}", id.getValue(), e);
            throw new RuntimeException("删除角色失败", e);
        }
    }

    @Override
    public boolean existsById(PersonaId id) {
        if (id == null) {
            return false;
        }

        try {
            return ctx()
                    .fetchExists(
                            ctx().selectOne()
                                    .from(PERSONA)
                                    .where(PERSONA.ID.eq(id.getValue()))
                    );
        } catch (Exception e) {
            log.error("检查角色存在性失败: id={}", id.getValue(), e);
            return false;
        }
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        try {
            return ctx()
                    .fetchExists(
                            ctx().selectOne()
                                    .from(PERSONA)
                                    .where(PERSONA.NAME.eq(name.trim()))
                    );
        } catch (Exception e) {
            log.error("检查角色名称存在性失败: name={}", name, e);
            return false;
        }
    }

    /**
     * 加载角色及其角色卡
     */
    private Optional<Persona> loadPersonaWithCard(PersonaPojo personaPojo) {
        try {
            PersonaCardPojo cardPojo = personaCardDao.ctx()
                    .selectFrom(PERSONA_CARD)
                    .where(PERSONA_CARD.PERSONA_ID.eq(personaPojo.getId()))
                    .fetchOneInto(PersonaCardPojo.class);

            return Optional.of(toDomain(personaPojo, cardPojo));
        } catch (Exception e) {
            log.error("加载角色卡失败: personaId={}", personaPojo.getId(), e);
            return Optional.empty();
        }
    }

    /**
     * 将数据库POJO转换为领域对象
     */
    private Persona toDomain(PersonaPojo personaPojo, PersonaCardPojo cardPojo) {
        PersonaId id = PersonaId.of(personaPojo.getId());
        PersonaCard card = cardPojo != null ? toPersonaCard(cardPojo) : null;

        // 使用Persona.create方法创建，然后根据enabled状态调整
        Persona persona = Persona.create(id, personaPojo.getName(), card, personaPojo.getDescription());

        // 如果数据库中是禁用状态，则禁用角色
        if (personaPojo.getEnabled() == null || personaPojo.getEnabled() != 1) {
            persona = persona.disable();
        }

        return persona;
    }

    /**
     * 转换PersonaCard
     */
    private PersonaCard toPersonaCard(PersonaCardPojo cardPojo) {
        return PersonaCard.of(
                cardPojo.getCharName(),
                cardPojo.getCharPersona(),
                cardPojo.getWorldScenario(),
                cardPojo.getCharGreeting(),
                cardPojo.getExampleDialogue()
        );
    }

    /**
     * 将领域对象转换为Persona POJO
     */
    private PersonaPojo fromDomainPersona(Persona persona) {
        PersonaPojo pojo = new PersonaPojo();
        pojo.setId(persona.getId().getValue());
        pojo.setName(persona.getName());
        pojo.setDescription(persona.getDescription());
        pojo.setWorldBookId(persona.getWorldBookId());
        pojo.setEnabled(persona.isEnabled() ? (byte) 1 : (byte) 0);
        pojo.setCreateTime(persona.getCreatedAt() != null ? persona.getCreatedAt() : LocalDateTime.now());
        pojo.setUpdateTime(LocalDateTime.now());
        return pojo;
    }

    /**
     * 将领域对象转换为PersonaCard POJO
     */
    private PersonaCardPojo fromDomainCard(Persona persona) {
        PersonaCardPojo pojo = new PersonaCardPojo();
        pojo.setPersonaId(persona.getId().getValue());
        
        if (persona.getCard() != null) {
            PersonaCard card = persona.getCard();
            pojo.setCharName(card.getCharName());
            pojo.setCharPersona(card.getCharPersona());
            pojo.setWorldScenario(card.getWorldScenario());
            pojo.setCharGreeting(card.getCharGreeting());
            pojo.setExampleDialogue(card.getExampleDialogue());
        }
        
        pojo.setCreateTime(LocalDateTime.now());
        pojo.setUpdateTime(LocalDateTime.now());
        return pojo;
    }
}
