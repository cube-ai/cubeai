class MessageArgumentList(dict):
    def __init__(self, role=None, m_type=None, m_name=None, tag=None, complexType=None):
        dict.__init__(self, role=role, type=m_type, name=m_name, tag=tag, complexType=complexType)

