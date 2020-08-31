

def update_tables(pool):
    # 更新数据库表结构，暂为空
    conn = pool.connection()
    with conn.cursor() as cursor:
        pass

    conn.close()
