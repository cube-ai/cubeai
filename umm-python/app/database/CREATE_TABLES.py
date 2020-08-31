

def create_tables(pool):
    conn = pool.connection()
    with conn.cursor() as cursor:
        create_table_solution(conn, cursor)
        create_table_artifact(conn, cursor)
        create_table_document(conn, cursor)
        create_table_comment(conn, cursor)
        create_table_composite_solution_map(conn, cursor)
        create_table_task(conn, cursor)
        create_table_task_step(conn, cursor)
        create_table_description(conn, cursor)
        create_table_deployment(conn, cursor)
        create_table_star(conn, cursor)
        create_table_credit(conn, cursor)
        create_table_credit_history(conn, cursor)
        create_table_composite_solution(conn, cursor)
        create_table_composite_deployment(conn, cursor)
    conn.close()


def create_table_solution(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS solution (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            author_login        varchar(255),
            author_name         varchar(255),
            company             varchar(255),
            name                varchar(255),
            version             varchar(255),
            summary             varchar(255),
            tag_1               varchar(255),
            tag_2               varchar(255),
            tag_3               varchar(255),
            subject_1           varchar(255),
            subject_2           varchar(255),
            subject_3           varchar(255),
            display_order       bigint,
            picture_url         longtext,
            active              bit,
            model_type          varchar(255),
            toolkit_type        varchar(255),
            star_count          bigint,
            view_count          bigint,
            download_count      bigint,
            comment_count       bigint,
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_artifact(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS artifact (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            solution_uuid       varchar(255),
            name                varchar(255),
            jhi_type            varchar(255),
            url                 varchar(512),
            file_size           bigint,
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_document(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS document (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            solution_uuid       varchar(255),
            author_login        varchar(255),
            name                varchar(255),
            url                 varchar(512),
            file_size           bigint,
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_comment(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS comment (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            user_login          varchar(255),
            solution_uuid       varchar(255),
            parent_uuid         varchar(255),
            comment_text        varchar(512),
            jhi_level           integer,
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()

    try:
        sql = 'CREATE UNIQUE INDEX idx_comment_uuid on comment(uuid);'
        cursor.execute(sql)
        conn.commit()
    except:
        pass


def create_table_composite_solution_map(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS composite_solution_map (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            parent_uuid         varchar(255),
            child_uuid          varchar(255)
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_task(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS task (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            task_name           varchar(255),
            task_type           varchar(255),
            task_status         varchar(255),
            task_progress       integer,
            description         varchar(512),
            target_uuid         varchar(255),
            user_login          varchar(255),
            start_date          timestamp,
            end_date            timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()
    try:
        sql = 'CREATE UNIQUE INDEX idx_task_uuid on task(uuid);'
        cursor.execute(sql)
        conn.commit()
    except:
        pass


def create_table_task_step(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS task_step (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            task_uuid             varchar(255),
            step_name             varchar(255),
            step_status           varchar(255),
            step_progress         integer,
            description           varchar(512),
            step_date             timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_description(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS description (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            solution_uuid         varchar(255),
            author_login          varchar(255),
            content               longtext
        );
    '''
    cursor.execute(sql)
    conn.commit()
    try:
        sql = 'CREATE UNIQUE INDEX idx_description_solution_uuid on description(solution_uuid);'
        cursor.execute(sql)
        conn.commit()
    except:
        pass


def create_table_deployment(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS deployment (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            deployer            varchar(255),
            solution_uuid       varchar(255),
            solution_name       varchar(255),
            solution_author     varchar(255),
            k_8_s_port          integer,
            is_public           bit,
            status              varchar(255),
            created_date        timestamp,
            modified_date       timestamp,
            picture_url         longtext,
            star_count          bigint,
            call_count          bigint,
            demo_url            varchar(512),
            subject_1           varchar(255),
            subject_2           varchar(255),
            subject_3           varchar(255),
            display_order       bigint
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_star(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS star (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            user_login          varchar(255),
            target_type         varchar(255),
            target_uuid         varchar(255),
            star_date           timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_credit(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS credit (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            user_login          varchar(255),
            credit              bigint
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_credit_history(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS credit_history (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            user_login          varchar(255),
            credit_plus         bigint,
            current_credit      bigint,
            jhi_comment         varchar(255),
            modify_date         timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_composite_solution(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS composite_solution (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            author_login        varchar(255),
            author_name         varchar(255),
            company             varchar(255),
            name                varchar(255),
            version             varchar(255),
            summary             varchar(255),
            tag_1               varchar(255),
            tag_2               varchar(255),
            tag_3               varchar(255),
            subject_1           varchar(255),
            subject_2           varchar(255),
            subject_3           varchar(255),
            display_order       bigint,
            picture_url         longtext,
            active              bit,
            model_type          varchar(255),
            toolkit_type        varchar(255),
            star_count          bigint,
            view_count          bigint,
            download_count      bigint,
            comment_count       bigint,
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_composite_deployment(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS composite_deployment (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            deployer            varchar(255),
            solution_uuid       varchar(255),
            solution_name       varchar(255),
            solution_author     varchar(255),
            k_8_s_port          integer,
            is_public           bit,
            status              varchar(255),
            created_date        timestamp,
            modified_date       timestamp,
            picture_url         longtext,
            star_count          bigint,
            call_count          bigint,
            demo_url            varchar(512),
            subject_1           varchar(255),
            subject_2           varchar(255),
            subject_3           varchar(255),
            display_order       bigint
        );
    '''
    cursor.execute(sql)
    conn.commit()
