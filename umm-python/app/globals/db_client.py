import pymysql
import tormysql
from app.database import CREATE_TABLES, UPDATE_TABLES


class DataBaseClient:
    def __init__(self, host, port, username, password, db_name):
        self.host = host
        self.port = port
        self.username = username
        self.password = password
        self.db_name = db_name
        self.pool = None

    def init_pool(self):
        self.pool = tormysql.ConnectionPool(
            host=self.host,
            port=self.port,
            user=self.username,
            passwd=self.password,
            db=self.db_name
        )

    def init_db(self):
        conn = pymysql.connect(
                self.host,
                port=self.port,
                user=self.username,
                passwd=self.password,
                database=self.db_name,
            )
        cursor = conn.cursor()
        CREATE_TABLES.create_tables(conn, cursor)
        UPDATE_TABLES.update_tables(conn, cursor)
        cursor.close()
        conn.close()

        self.init_pool()
