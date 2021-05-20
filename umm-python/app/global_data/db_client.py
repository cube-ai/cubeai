import pymysql
from DBUtils.PooledDB import PooledDB
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
        self.pool = PooledDB(
            pymysql,
            mincached=5,
            host=self.host,
            port=self.port,
            user=self.username,
            passwd=self.password,
            db=self.db_name
        )

    def init_db(self):
        self.init_pool()
        CREATE_TABLES.create_tables(self.pool)
        UPDATE_TABLES.update_tables(self.pool)
