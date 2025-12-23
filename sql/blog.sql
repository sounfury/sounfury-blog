create table blog.article
(
    id            int unsigned auto_increment comment '主键'
        primary key,
    title         varchar(256)                           not null comment '标题',
    content       longtext                               null comment '文章内容（Markdown格式）',
    summary       varchar(1024)                          null comment '文章摘要',
    category_id   int unsigned                           not null comment '所属分类ID',
    thumbnail     varchar(256)                           null comment '缩略图',
    is_top        tinyint      default 0                 not null comment '是否置顶（0否，1是）',
    enable_status tinyint      default 0                 not null comment '状态（0草稿，1已发布）',
    view_count    int unsigned default '0'               null comment '访问量',
    is_comment    tinyint      default 1                 not null comment '是否允许评论（1是，0否）',
    create_by     varchar(128)                           null comment '创建人',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by     varchar(128)                           null comment '更新人',
    update_time   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag      tinyint      default 0                 not null comment '删除标志（0未删除，1已删除）'
)
    comment '文章表' row_format = DYNAMIC;

create index idx_category_id
    on blog.article (category_id);

create table blog.article_tag
(
    id          int unsigned auto_increment comment '主键'
        primary key,
    article_id  int unsigned                       not null comment '文章ID',
    tag_id      int unsigned                       not null comment '标签ID',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    tinyint  default 0                 not null comment '删除标识',
    constraint uk_article_tag
        unique (article_id, tag_id)
)
    comment '文章标签关联表' row_format = DYNAMIC;

create index fk_article_tag_tag
    on blog.article_tag (tag_id);

create table blog.category
(
    id            int unsigned auto_increment comment '主键'
        primary key,
    name          varchar(128)                       not null comment '分类名',
    pid           int unsigned                       null comment '父分类ID（-1表示无父分类）',
    description   varchar(512)                       null comment '描述',
    enable_status tinyint  default 1                 not null comment '状态（0禁用，1启用）',
    create_by     int unsigned                       null comment '创建人ID',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     int unsigned                       null comment '更新人ID',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag      tinyint  default 0                 not null comment '删除标志（0未删除，1已删除）',
    `order`       int      default 1                 not null comment '顺序'
)
    comment '分类表' row_format = DYNAMIC;

create table blog.permission
(
    id   int unsigned auto_increment
        primary key,
    code varchar(255) not null,
    name varchar(255) not null
)
    row_format = DYNAMIC;

create table blog.role
(
    id   int unsigned auto_increment
        primary key,
    code varchar(255) not null,
    name varchar(255) not null
)
    row_format = DYNAMIC;

create table blog.role_permission_map
(
    id            int unsigned auto_increment
        primary key,
    role_id       int unsigned not null,
    permission_id int unsigned not null
)
    row_format = DYNAMIC;

create table blog.site_creator_info
(
    id                 tinyint  default 1                 not null comment '唯一ID，始终为1'
        primary key,
    nick_name          varchar(64)                        not null comment '昵称',
    avatar_url         varchar(256)                       not null comment '头像URL',
    author_description varchar(512)                       null comment '作者描述',
    homepage_url       varchar(256)                       null comment '作者主页URL',
    create_time        datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time        datetime default CURRENT_TIMESTAMP not null comment '更新时间'
)
    comment '博客作者信息表' row_format = DYNAMIC;

create table blog.site_info
(
    article_count      int unsigned    null comment '文章总数',
    total_words        bigint unsigned null comment '总字数',
    total_visits       int unsigned    null comment '总访问量',
    last_update_time   datetime        null on update CURRENT_TIMESTAMP comment '最后更新时间',
    site_creation_date datetime        null comment '网站创建时间',
    id                 tinyint auto_increment comment '主键'
        primary key,
    constraint site_creation_date
        unique (site_creation_date)
)
    comment '博客网站全局信息表' row_format = DYNAMIC;

create table blog.sys_config
(
    config_id    int auto_increment comment '设置ID'
        primary key,
    config_key   varchar(100)                           not null comment '设置键名',
    config_value text                                   not null comment '设置值',
    description  varchar(255)                           null comment '设置项描述',
    updated_time timestamp    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    created_time datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    del_flag     tinyint      default 0                 not null comment '删除标识',
    config_name  varchar(200) default ''                not null comment '''参数名称''',
    constraint setting_key
        unique (config_key),
    constraint sys_config_pk
        unique (config_name)
)
    comment '网站全局设置表' row_format = DYNAMIC;

create table blog.sys_oss
(
    oss_id        int unsigned auto_increment comment '对象存储主键'
        primary key,
    file_name     varchar(255) default ''                not null comment '文件名',
    original_name varchar(255) default ''                not null comment '原名',
    file_suffix   varchar(10)  default ''                not null comment '文件后缀名',
    url           varchar(500)                           not null comment 'URL地址',
    create_time   datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    create_by     int unsigned                           null comment '上传人',
    update_time   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by     int unsigned                           null comment '更新人',
    service       varchar(20)  default 'minio'           not null comment '服务商'
)
    comment 'OSS对象存储表' row_format = DYNAMIC;

create table blog.sys_oss_config
(
    oss_config_id int unsigned auto_increment comment '主键'
        primary key,
    config_key    varchar(20)  default ''                not null comment '配置key(服务商)',
    access_key    varchar(255) default ''                null comment 'accessKey',
    secret_key    varchar(255) default ''                null comment '秘钥',
    bucket_name   varchar(255) default ''                null comment '桶名称',
    prefix        varchar(255) default ''                null comment '前缀',
    endpoint      varchar(255) default ''                null comment '访问站点',
    domain        varchar(255) default ''                null comment '自定义域名',
    is_https      tinyint      default 1                 null comment '是否https（Y=是,N=否）',
    region        varchar(255) default ''                null comment '域',
    access_policy tinyint      default 1                 not null comment '桶权限类型(0=private 1=public 2=custom)',
    create_by     int unsigned                           null comment '创建者',
    create_time   datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     int unsigned                           null comment '更新者',
    update_time   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    remark        varchar(500)                           null comment '备注',
    enable_status tinyint      default 1                 not null comment '(0禁用，1启用)，注意只能有一个被启用',
    constraint sys_oss_config_pk
        unique (config_key)
)
    comment '对象存储配置表' row_format = DYNAMIC;

create table blog.tag
(
    id          int unsigned auto_increment comment '主键'
        primary key,
    name        varchar(128)                       not null comment '标签名',
    create_by   int unsigned                       null comment '创建人ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   int unsigned                       null comment '更新人ID',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    tinyint  default 0                 not null comment '删除标志（0未删除，1已删除）'
)
    comment '标签表' row_format = DYNAMIC;

create table blog.theme_settings
(
    theme_id      int auto_increment comment '主题ID'
        primary key,
    theme_key     varchar(128)                        null,
    theme_name    varchar(100)                        not null comment '主题名称',
    settings      json                                not null comment '主题配置项 (以JSON存储)',
    description   varchar(255)                        null comment '主题描述',
    updated_time  timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    created_time  timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    mode          tinyint   default 1                 not null comment '是否显示顶图 0不显示，1显示',
    del_flag      tinyint   default 0                 not null comment '删除',
    enable_status tinyint   default 1                 not null comment '0未启用 1启用',
    constraint theme_name
        unique (theme_name),
    constraint theme_settings_pk
        unique (theme_key)
)
    comment '主题配置表' row_format = DYNAMIC;

create table blog.user
(
    id            int unsigned auto_increment
        primary key,
    username      varchar(255)                          not null,
    password      varchar(255)                          not null,
    enable_status tinyint(1)  default 1                 not null comment '0禁用1启用',
    nickname      varchar(50) default '`username`'      null comment '昵称',
    mail          varchar(128)                          not null comment '邮箱',
    create_time   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    del_flag      tinyint     default 0                 not null comment '删除标识 0：未删除 1：已删除',
    avatar        varchar(256)                          null comment '用户头像',
    constraint uk_username
        unique (username)
)
    row_format = DYNAMIC;

create table blog.comment
(
    id             int unsigned auto_increment comment '评论ID'
        primary key,
    article_id     int unsigned                           not null comment '关联的文章ID',
    user_id        int unsigned                           not null comment '发表评论的用户ID',
    parent_id      int unsigned                           null comment '父评论ID（为空表示一级评论）',
    content        text                                   not null comment '评论内容',
    like_count     int unsigned default '0'               null comment '点赞数',
    enable_status  tinyint      default 1                 not null comment '状态（0未启用，1正常）',
    create_time    datetime     default CURRENT_TIMESTAMP null comment '评论创建时间',
    update_time    datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '评论更新时间',
    del_flag       tinyint      default 0                 not null,
    top_comment_id int unsigned                           null comment '顶层评论id',
    constraint fk_comment_article
        foreign key (article_id) references blog.article (id),
    constraint fk_comment_user
        foreign key (user_id) references blog.user (id)
)
    comment '评论表' row_format = DYNAMIC;

create index idx_article_id
    on blog.comment (article_id);

create index idx_parent_id
    on blog.comment (parent_id);

create index idx_user_id
    on blog.comment (user_id);

create table blog.user_role_map
(
    id      int unsigned auto_increment
        primary key,
    user_id int unsigned not null,
    role_id int unsigned not null
)
    row_format = DYNAMIC;

