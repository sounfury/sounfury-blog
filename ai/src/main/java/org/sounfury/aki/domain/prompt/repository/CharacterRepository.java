package org.sounfury.aki.domain.prompt.repository;

import org.sounfury.aki.application.prompt.persona.dto.PersonaPageRequest;
import org.sounfury.aki.domain.prompt.persona.Persona;
import org.sounfury.aki.domain.prompt.persona.PersonaId;
import org.sounfury.jooq.page.PageRepDto;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓储接口
 * 定义角色聚合的持久化操作
 */
public interface CharacterRepository {
    
    /**
     * 根据ID查找角色
     * @param id 角色ID
     * @return 角色实体，如果不存在则返回空
     */
    Optional<Persona> findPersonaById(PersonaId id);
    
    /**
     * 根据名称查找角色
     * @param name 角色名称
     * @return 角色实体，如果不存在则返回空
     */
    Optional<Persona> findByName(String name);
    
    /**
     * 查找所有启用的角色
     * @return 启用的角色列表
     */
    List<Persona> findAllEnabled();
    
    /**
     * 查找所有角色
     * @return 所有角色列表
     */
    List<Persona> findAllPersona();
    
    /**
     * 保存角色
     * @param persona 角色实体
     * @return 保存后的角色实体
     */
    Persona save(Persona persona);
    
    /**
     * 删除角色
     * @param id 角色ID
     */
    void deleteById(PersonaId id);
    
    /**
     * 检查角色是否存在
     * @param id 角色ID
     * @return 如果存在返回true，否则返回false
     */
    boolean existsById(PersonaId id);
    
    /**
     * 检查角色名称是否存在
     * @param name 角色名称
     * @return 如果存在返回true，否则返回false
     */
    boolean existsByName(String name);
    
    /**
     * 分页查询角色列表
     * @param request 分页查询请求
     * @return 分页查询结果
     */
    PageRepDto<List<Persona>> findPersonaPage(PersonaPageRequest request);
    
    /**
     * 根据ID查找角色详细信息（包含完整的角色卡信息）
     * @param id 角色ID
     * @return 角色详细信息，如果不存在则返回空
     */
    Optional<Persona> findPersonaDetailById(PersonaId id);
}
