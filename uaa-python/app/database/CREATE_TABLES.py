from datetime import datetime


def create_tables(pool):
    conn = pool.connection()
    with conn.cursor() as cursor:
        create_table_user(conn, cursor)
        create_table_authority(conn, cursor)
        create_table_verify_code(conn, cursor)
        create_table_message(conn, cursor)
        create_table_article(conn, cursor)
        create_table_attachment(conn, cursor)
        create_table_application(conn, cursor)
    conn.close()


def create_table_user(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS user (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            login               varchar(50) NOT NULL UNIQUE,
            password_hash       varchar(60),
            full_name           varchar(50),
            phone               varchar(50) NOT NULL UNIQUE,
            email               varchar(100) NOT NULL UNIQUE,
            image_url           longtext,
            activated           bit NOT NULL,
            lang_key            varchar(6),
            activation_key      varchar(20),
            reset_key           varchar(20),
            created_by          varchar(50),
            created_date        timestamp,
            reset_date          timestamp,
            last_modified_by    varchar(50),
            last_modified_date  timestamp,
            authorities         varchar(1200)
        );
    '''
    cursor.execute(sql)
    conn.commit()
    try:
        sql = 'CREATE UNIQUE INDEX idx_user_login on user(login);'
        cursor.execute(sql)
        conn.commit()
    except:
        pass
    try:
        sql = 'CREATE UNIQUE INDEX idx_user_email on user(email);'
        cursor.execute(sql)
        conn.commit()
    except:
        pass
    try:
        sql = 'CREATE UNIQUE INDEX idx_user_phone on user(phone);'
        cursor.execute(sql)
        conn.commit()
    except:
        pass

    sql = 'SELECT COUNT(*) FROM user where login = "admin"'
    cursor.execute(sql)
    count = cursor.fetchone()[0]
    if count < 1:
        sql = '''
            INSERT INTO user (
                login,
                password_hash,
                full_name,
                phone,
                email,
                activated,
                lang_key,
                created_by,
                created_date,
                last_modified_by,
                last_modified_date,
                authorities
            ) VALUES ("admin", "admin", 
                      "admin", "18600000000", "admin@localhost", 1, "en", 
                      "system", "{}", "system", "{}", "ROLE_ADMIN,ROLE_USER")
        '''.format(datetime.now().strftime('%Y-%m-%dT%H:%M:%S'), datetime.now().strftime('%Y-%m-%dT%H:%M:%S'))
        cursor.execute(sql)
        conn.commit()


def create_table_authority(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS authority (
            name  varchar(50) PRIMARY KEY  NOT NULL
        );
    '''
    cursor.execute(sql)
    conn.commit()

    sql = 'SELECT COUNT(*) FROM authority'
    cursor.execute(sql)
    count = cursor.fetchone()[0]
    if count < 1:
        sql = 'INSERT INTO authority (name) VALUES ( "ROLE_ADMIN")'
        cursor.execute(sql)
        sql = 'INSERT INTO authority (name) VALUES ( "ROLE_USER")'
        cursor.execute(sql)
        sql = 'INSERT INTO authority (name) VALUES ( "ROLE_MANAGER")'
        cursor.execute(sql)
        sql = 'INSERT INTO authority (name) VALUES ( "ROLE_OPERATOR")'
        cursor.execute(sql)
        sql = 'INSERT INTO authority (name) VALUES ( "ROLE_CONTENT")'
        cursor.execute(sql)
        sql = 'INSERT INTO authority (name) VALUES ( "ROLE_APPLICATION")'
        cursor.execute(sql)
        conn.commit()


def create_table_verify_code(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS verify_code (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            code                varchar(255),
            expire              timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_message(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS message (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            sender              varchar(255),
            receiver            varchar(255),
            subject             varchar(255),
            content             varchar(1024),
            url                 varchar(512),
            urgent              bit,
            viewed              bit,
            deleted             bit,
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_article(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS article (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            author_login        varchar(255),
            author_name         varchar(255),
            subject_1           varchar(255),
            subject_2           varchar(255),
            subject_3           varchar(255),
            title               varchar(255),
            summary             varchar(255),
            tag_1               varchar(255),
            tag_2               varchar(255),
            tag_3               varchar(255),            
            picture_url         varchar(512),
            content             longtext,
            display_order       bigint,
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()


def create_table_attachment(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS attachment (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
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


def create_table_application(conn, cursor):
    sql = '''
        CREATE TABLE IF NOT EXISTS application (
            id  bigint PRIMARY KEY  NOT NULL AUTO_INCREMENT,
            uuid                varchar(255),
            name                varchar(255),
            summary             varchar(255),
            url                 varchar(512),            
            picture_url         longtext,
            subject_1           varchar(255),
            subject_2           varchar(255),
            subject_3           varchar(255),
            owner               varchar(255),
            need_roles          varchar(255),
            display_order       bigint,
            created_by          varchar(255),
            modified_by         varchar(255),
            created_date        timestamp,
            modified_date       timestamp
        );
    '''
    cursor.execute(sql)
    conn.commit()
